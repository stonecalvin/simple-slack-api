package com.ullink.slack.simpleslackapi.impl;

import com.google.common.eventbus.Subscribe;
import com.ullink.slack.simpleslackapi.ChannelHistoryModule;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.*;
import com.ullink.slack.simpleslackapi.replies.GenericSlackReply;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelHistoryModuleImpl implements ChannelHistoryModule {

    private final SlackSession session;
    private static final String FETCH_CHANNEL_HISTORY_COMMAND = "channels.history";
    private static final String FETCH_GROUP_HISTORY_COMMAND = "groups.history";
    private static final String FETCH_IM_HISTORY_COMMAND = "im.history";
    private static final int DEFAULT_HISTORY_FETCH_SIZE = 1000;

    public ChannelHistoryModuleImpl(SlackSession session) {
        this.session = session;
    }

    @Override
    public List<SlackMessagePosted> fetchHistoryOfChannel(String channelId) {
        return fetchHistoryOfChannel(channelId, null, -1);
    }

    @Override
    public List<SlackMessagePosted> fetchHistoryOfChannel(String channelId, LocalDate day) {
        return fetchHistoryOfChannel(channelId, day, -1);
    }

    @Override
    public List<SlackMessagePosted> fetchHistoryOfChannel(String channelId, int numberOfMessages) {
        return fetchHistoryOfChannel(channelId, null, numberOfMessages);
    }

    @Override
    public List<SlackMessagePosted> fetchHistoryOfChannel(String channelId, LocalDate day, int numberOfMessages) {
        Map<String, String> params = new HashMap<>();
        params.put("channel", channelId);
        if (day != null) {
            ZonedDateTime start = ZonedDateTime.of(day.atStartOfDay(), ZoneId.of("UTC"));
            ZonedDateTime end = ZonedDateTime.of(day.atStartOfDay().plusDays(1).minus(1, ChronoUnit.MILLIS), ZoneId.of("UTC"));
            params.put("oldest", convertDateToSlackTimestamp(start));
            params.put("latest", convertDateToSlackTimestamp(end));
        }
        if (numberOfMessages > -1) {
            params.put("count", String.valueOf(numberOfMessages));
        } else {
            params.put("count", String.valueOf(DEFAULT_HISTORY_FETCH_SIZE));
        }
        SlackChannel channel = session.findChannelById(channelId);
        switch (channel.getType()) {
            case INSTANT_MESSAGING:
                return fetchHistoryOfChannel(params,FETCH_IM_HISTORY_COMMAND);
            case PRIVATE_GROUP:
                return fetchHistoryOfChannel(params,FETCH_GROUP_HISTORY_COMMAND);
            default:
                return fetchHistoryOfChannel(params,FETCH_CHANNEL_HISTORY_COMMAND);
        }
    }

    private List<SlackMessagePosted> fetchHistoryOfChannel(Map<String, String> params, String command) {
        SlackMessageHandle<GenericSlackReply> handle = session.postGenericSlackCommand(params, command);
        GenericSlackReply replyEv = handle.getReply();
        JSONObject answer = replyEv.getPlainAnswer();
        JSONArray events = (JSONArray) answer.get("messages");
        List<SlackMessagePosted> messages = new ArrayList<>();
        if (events != null) {
            for (Object event : events) {
                if ((((JSONObject) event).get("subtype") == null)) {
                    String jsonStr = ((JSONObject) event).toJSONString();

                    messages.add(ImmutableSlackMessagePosted
                            .copyOf(session.getGson().fromJson(jsonStr, SlackMessagePosted.class))
                            .withSlackSession(session));
                }
            }
        }
        return messages;
    }

    @Override
    public List<SlackMessagePosted> fetchUpdatingHistoryOfChannel(String channelId) {
        return fetchUpdatingHistoryOfChannel(channelId, null, -1);
    }

    @Override
    public List<SlackMessagePosted> fetchUpdatingHistoryOfChannel(String channelId, LocalDate day) {
        return fetchUpdatingHistoryOfChannel(channelId, day, -1);
    }

    @Override
    public List<SlackMessagePosted> fetchUpdatingHistoryOfChannel(String channelId, int numberOfMessages) {
        return fetchUpdatingHistoryOfChannel(channelId, null, numberOfMessages);
    }

    @Override
    public List<SlackMessagePosted> fetchUpdatingHistoryOfChannel(String channelId, LocalDate day, int numberOfMessages) {
        List<SlackMessagePosted> messages = fetchHistoryOfChannel(channelId, day, numberOfMessages);

        session.registerListener(new ChannelHistoryReactionAddedListener(messages));
        session.registerListener(new ChannelHistoryReactionRemovedListener(messages));
        session.registerListener(new ChannelHistoryMessagePostedListener(messages));
        return messages;
    }

    public class ChannelHistoryReactionAddedListener {

        List<SlackMessagePosted> messages = new ArrayList<>();

        public ChannelHistoryReactionAddedListener(List<SlackMessagePosted> initialMessages) {
            messages = initialMessages;
        }

        // TODO: Possibly fix logic for this.
        // This method seems buggy to me. What is it really trying to do?
        // Seems like it adds 1 to the reaction count of whatever the first message in history is,
        // Or 1 to all messages in history if no match is found while iterating.
        //
        // In my mind this is actually supposed to just add 1 to the emoji count of the "correct" message?
        @Subscribe
        public void onEvent(ReactionAdded event) {
            String emojiName = event.getReaction();
            for (SlackMessagePosted message : messages) {
                if (!message.getReactions().isPresent()) {
                    continue;
                }

                Map<String, Integer> reactions = message.getReactions().get();
                for (String reaction : reactions.keySet()) {
                    if (emojiName.equals(reaction)) {
                        int count = reactions.get(emojiName);
                        reactions.put(emojiName, count++);
                        return;
                    }
                }
                reactions.put(emojiName, 1);
            }
        }
    }

    public class ChannelHistoryReactionRemovedListener {

        List<SlackMessagePosted> messages = new ArrayList<>();

        public ChannelHistoryReactionRemovedListener(List<SlackMessagePosted> initialMessages) {
            messages = initialMessages;
        }

        // TODO: Possibly fix logic for this.
        // This one seems better, but still not sure how it knows that it is modifying the emoji count on
        // the correct message.
        @Subscribe
        public void onEvent(ReactionRemoved event) {
            String emojiName = event.getReaction();

            for (SlackMessagePosted message : messages) {
                if (!message.getReactions().isPresent()) {
                    continue;
                }

                Map<String, Integer> reactions = message.getReactions().get();

                for (String reaction : reactions.keySet()) {
                    if (emojiName.equals(reaction)) {
                        int count = reactions.get(emojiName);
                        if (count == 1) {
                            reactions.remove(emojiName);
                        } else {
                            reactions.put(emojiName, --count);
                        }
                        return;
                    }
                }
            }
        }
    }

    public class ChannelHistoryMessagePostedListener {

        List<SlackMessagePosted> messages = new ArrayList<>();

        public ChannelHistoryMessagePostedListener(List<SlackMessagePosted> initialMessages) {
            messages = initialMessages;
        }

        @Subscribe
        public void onEvent(SlackMessagePosted event) {
            messages.add(event);
        }
    }

    private String convertDateToSlackTimestamp(ZonedDateTime date) {
        return (date.toInstant().toEpochMilli() / 1000) + ".123456";
    }

}
