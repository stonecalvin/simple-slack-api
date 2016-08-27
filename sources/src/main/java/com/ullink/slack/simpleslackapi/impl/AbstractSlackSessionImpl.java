package com.ullink.slack.simpleslackapi.impl;

import com.google.common.eventbus.EventBus;
import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.replies.SlackMessageReply;

import java.util.*;
import java.util.stream.Collectors;

abstract class AbstractSlackSessionImpl implements SlackSession
{

    protected Map<String, SlackChannel>            channels                 = new HashMap<>();
    protected Map<String, SlackUser>               users                    = new HashMap<>();
    protected Map<String, SlackIntegration>        integrations             = new HashMap<>();
    protected SlackPersona                         sessionPersona;
    protected SlackTeam                            team;

    protected EventBus eventBus = new EventBus("SlackJavaApiEventBus");

    static final SlackChatConfiguration            DEFAULT_CONFIGURATION    = SlackChatConfiguration.getConfiguration().asUser();
    static final boolean                           DEFAULT_UNFURL           = true;

    @Override
    public SlackTeam getTeam()
    {
        return team;
    }

    @Override
    public Collection<SlackChannel> getChannels()
    {
        return new ArrayList<>(channels.values());
    }

    @Override
    public Collection<SlackUser> getUsers()
    {
        return new ArrayList<>(users.values());
    }

    @Override
    public Collection<SlackIntegration> getIntegrations() {
        return new ArrayList<>(integrations.values());
    }

    @Override
    @Deprecated
    public Collection<SlackBot> getBots() {
        return users.values().stream()
                .filter(SlackUser::isBot)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SlackChannel> findChannelByName(String channelName) {
        return channels.values().stream()
                .filter(channel -> channel.getName().equals(channelName))
                .findFirst();
    }

    @Override
    public SlackChannel findChannelById(String channelId)
    {
        SlackChannel toReturn = channels.get(channelId);
        if (toReturn == null)
        {
            // direct channel case
            if (channelId != null && channelId.startsWith("D"))
            {
                toReturn = new SlackChannelImpl(channelId, "", "", "", true, false);
            }
        }
        return toReturn;
    }

    @Override
    public SlackUser findUserById(String userId)
    {
        return users.get(userId);
    }

    @Override
    public Optional<SlackUser> findUserByUserName(String userName) {
        return users.values().stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst();
    }

    @Override
    public Optional<SlackUser> findUserByEmail(String userMail) {
        return users.values().stream()
                .filter(user -> userMail.equals(user.getUserMail()))
                .findFirst();
    }

    @Override
    public SlackIntegration findIntegrationById(String integrationId)
    {
        return integrations.get(integrationId);
    }

    @Override
    public SlackPersona sessionPersona()
    {
        return sessionPersona;
    }

    @Override
    @Deprecated
    public SlackBot findBotById(String botId)
    {
        return users.get(botId);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment)
    {
        return sendMessage(channel, message, attachment, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(SlackChannel channel, String message)
    {
        return sendMessage(channel, message, DEFAULT_UNFURL);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(SlackChannel channel, String message, boolean unfurl)
    {
        SlackPreparedMessage preparedMessage = new SlackPreparedMessage.Builder()
                .withMessage(message)
                .withUnfurl(unfurl)
                .build();
        return sendMessage(channel, preparedMessage, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment, boolean unfurl)
    {
        return sendMessage(channel, message, attachment, DEFAULT_CONFIGURATION, unfurl);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration)
    {
        return sendMessage(channel, message, attachment, chatConfiguration, DEFAULT_UNFURL);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(SlackChannel channel, SlackPreparedMessage preparedMessage) {
        return sendMessage(channel, preparedMessage, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration, boolean unfurl)
    {
        SlackPreparedMessage preparedMessage = new SlackPreparedMessage.Builder()
                .withMessage(message)
                .withUnfurl(unfurl)
                .addAttachment(attachment)
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
