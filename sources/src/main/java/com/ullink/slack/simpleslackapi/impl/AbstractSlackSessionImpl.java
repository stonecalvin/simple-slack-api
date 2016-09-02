package com.ullink.slack.simpleslackapi.impl;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackPreparedMessage;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.json.*;
import com.ullink.slack.simpleslackapi.replies.SlackMessageReply;

import java.util.*;

abstract class AbstractSlackSessionImpl implements SlackSession
{

    protected Map<String, Channel> channels = new HashMap<>();
    protected Map<String, User> users = new HashMap<>();
    protected Map<String, Integration> integrations = new HashMap<>();
    protected User sessionPersona;
    protected Team team;
    protected Gson gson;

    protected EventBus eventBus = new EventBus("SlackJavaApiEventBus");

    static final SlackChatConfiguration DEFAULT_CONFIGURATION = SlackChatConfiguration.getConfiguration().asUser();
    static final boolean DEFAULT_UNFURL = true;

    @Override
    public Gson getGson() {
        return gson;
    }

    @Override
    public Team getTeam()
    {
        return team;
    }

    @Override
    public Collection<Channel> getChannels()
    {
        return new ArrayList<>(channels.values());
    }

    @Override
    public Collection<User> getUsers()
    {
        return new ArrayList<>(users.values());
    }

    @Override
    public Collection<Integration> getIntegrations() {
        return new ArrayList<>(integrations.values());
    }

     @Override
    public Optional<Channel> findChannelByName(String channelName) {
        return channels.values().stream()
                .filter(channel -> channel.name().isPresent())
                .filter(channel -> channel.name().get().equals(channelName))
                .findFirst();
    }

    @Override
    public Channel findChannelById(String channelId)
    {
        Channel toReturn = channels.get(channelId);
        if (toReturn == null)
        {
            // direct channel case
            if (channelId != null && channelId.startsWith("D"))
            {
                toReturn = ImmutableChannel.builder()
                        .isIm(true)
                        .id(channelId)
                        .build();
            }
        }
        return toReturn;
    }

    @Override
    public User findUserById(String userId)
    {
        return users.get(userId);
    }

    @Override
    @Deprecated
    public User findBotById(String botId) {
        return users.get(botId);
    }

    @Override
    public Optional<User> findUserByUserName(String userName) {
        return users.values().stream()
                .filter(user -> user.name().equals(userName))
                .findFirst();
    }

    @Override
    public Optional<User> findUserByEmail(String userMail) {
        return users.values().stream()
                .filter(user -> user.profile().isPresent())
                .filter(user -> userMail.equals(user.profile().get().email()))
                .findFirst();
    }

    @Override
    public Integration findIntegrationById(String integrationId)
    {
        return integrations.get(integrationId);
    }

    @Override
    public User sessionPersona()
    {
        return sessionPersona;
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, SlackAttachment attachment)
    {
        return sendMessage(channel, message, attachment, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message)
    {
        return sendMessage(channel, message, DEFAULT_UNFURL);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, boolean unfurl)
    {
        SlackPreparedMessage preparedMessage = new SlackPreparedMessage.Builder()
                .withMessage(message)
                .withUnfurl(unfurl)
                .build();
        return sendMessage(channel, preparedMessage, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, SlackAttachment attachment, boolean unfurl)
    {
        return sendMessage(channel, message, attachment, DEFAULT_CONFIGURATION, unfurl);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration)
    {
        return sendMessage(channel, message, attachment, chatConfiguration, DEFAULT_UNFURL);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, SlackPreparedMessage preparedMessage) {
        return sendMessage(channel, preparedMessage, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration, boolean unfurl)
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
