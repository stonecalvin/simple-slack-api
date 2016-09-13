package com.ullink.slack.simpleslackapi.impl;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.replies.SlackParsedReply;

import java.util.*;

abstract class AbstractSlackSessionImpl implements SlackSession {

    protected Map<String, SlackChannel> channels = new HashMap<>();
    protected Map<String, SlackUser> users = new HashMap<>();
    protected Map<String, SlackIntegration> integrations = new HashMap<>();
    protected SlackUser sessionPersona;
    protected SlackTeam slackTeam;
    protected Gson gson;

    protected EventBus eventBus = new EventBus("SlackJavaApiEventBus");

    static final SlackChatConfiguration DEFAULT_CONFIGURATION = SlackChatConfiguration.getConfiguration().asUser();
    static final boolean DEFAULT_UNFURL = true;

    @Override
    public Gson getGson() {
        return gson;
    }

    public SlackTeam getSlackTeam() {
        return slackTeam;
    }

    @Override
    public Collection<SlackChannel> getChannels() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public Collection<SlackUser> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Collection<SlackIntegration> getIntegrations() {
        return new ArrayList<>(integrations.values());
    }

    @Override
    public Optional<SlackChannel> findChannelByName(String channelName) {
        return channels.values().stream()
                .filter(channel -> channel.getName().isPresent())
                .filter(channel -> channel.getName().get().equals(channelName))
                .findFirst();
    }

    @Override
    public SlackChannel findChannelById(String channelId) {
        SlackChannel toReturn = channels.get(channelId);
        if (toReturn == null) {
            // direct channel case
            if (channelId != null && channelId.startsWith("D")) {
                toReturn = ImmutableSlackChannel.builder()
                        .isIm(true)
                        .id(channelId)
                        .build();
            }
        }
        return toReturn;
    }

    @Override
    public SlackUser findUserById(String userId) {
        return users.get(userId);
    }

    @Override
    @Deprecated
    public SlackUser findBotById(String botId) {
        return users.get(botId);
    }

    @Override
    public Optional<SlackUser> findUserByUserName(String userName) {
        return users.values().stream()
                .filter(user -> user.getName().equals(userName))
                .findFirst();
    }

    @Override
    public Optional<SlackUser> findUserByEmail(String userMail) {
        return users.values().stream()
                .filter(user -> user.getProfile().isPresent())
                .filter(user -> userMail.equals(user.getProfile().get().getEmail().get()))
                .findFirst();
    }

    @Override
    public SlackIntegration findIntegrationById(String integrationId) {
        return integrations.get(integrationId);
    }

    @Override
    public SlackUser sessionPersona() {
        return sessionPersona;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment) {
        return sendMessage(channel, message, attachment, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message) {
        return sendMessage(channel, message, DEFAULT_UNFURL);
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, boolean unfurl) {
        SlackPreparedMessage preparedMessage = ImmutableSlackPreparedMessage.builder()
                .message(message)
                .isUnfurl(unfurl)
                .build();

        return sendMessage(channel, preparedMessage, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment, boolean unfurl) {
        return sendMessage(channel, message, attachment, DEFAULT_CONFIGURATION, unfurl);
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, SlackPreparedMessage preparedMessage) {
        return sendMessage(channel, preparedMessage, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration) {
        return sendMessage(channel, message, attachment, chatConfiguration, false);
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration, boolean unfurl)
    {
        SlackPreparedMessage preparedMessage = ImmutableSlackPreparedMessage.builder()
                .message(message)
                .isUnfurl(unfurl)
                .attachments(Collections.singletonList(attachment))
                .build();

        return sendMessage(channel, preparedMessage, chatConfiguration);
    }

    @Override
    public void registerListener(Object listener) {
        eventBus.register(listener);
    }

    @Override
    public void unregisterListener(Object listener) {
        eventBus.unregister(listener);
    }

}
