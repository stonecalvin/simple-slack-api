package com.ullink.slack.simpleslackapi;

import com.google.gson.Gson;
import com.ullink.slack.simpleslackapi.impl.SlackChatConfiguration;
import com.ullink.slack.simpleslackapi.json.*;
import com.ullink.slack.simpleslackapi.replies.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface SlackSession {
    void registerListener(Object listener);
    void unregisterListener(Object listener);

    Gson getGson();

    Team getTeam();

    Collection<Channel> getChannels();

    Collection<User> getUsers();

    Collection<Integration> getIntegrations();

    Optional<Channel> findChannelByName(String channelName);

    Channel findChannelById(String channelId);

    Integration findIntegrationById(String integrationId);

    User findUserById(String userId);

    User findBotById(String botId);

    Optional<User> findUserByUserName(String userName);

    Optional<User> findUserByEmail(String userMail);

    User sessionPersona();

    SlackMessageHandle<EmojiSlackReply> listEmoji();

    void refetchUsers();

    Presence getPresence(User persona);

    void setPresence(Presence presence);

    void connect() throws IOException;

    void disconnect() throws IOException;

    SlackMessageHandle<ParsedSlackReply> inviteUser(String email, String firstName, boolean setActive);

    SlackMessageHandle<SlackMessageReply> deleteMessage(String timestamp, Channel channel);

    SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, SlackPreparedMessage preparedMessage, SlackChatConfiguration chatConfiguration);

    SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, SlackPreparedMessage preparedMessage);

    SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration, boolean unfurl);

    SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration);

    SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, SlackAttachment attachment, boolean unfurl);

    SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, SlackAttachment attachment);

    SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message, boolean unfurl);

    SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, String message);

    SlackMessageHandle<SlackMessageReply> sendFile(Channel channel, byte [] data, String fileName);

    SlackMessageHandle<SlackMessageReply> sendMessageToUser(User user, String message, SlackAttachment attachment);

    SlackMessageHandle<SlackMessageReply> sendMessageToUser(String userName, String message, SlackAttachment attachment);

    SlackMessageHandle<SlackMessageReply> updateMessage(String timestamp, Channel channel, String message);

    SlackMessageHandle<SlackMessageReply> sendMessageOverWebSocket(Channel channel, String message);

    SlackMessageHandle<SlackMessageReply> addReactionToMessage(Channel channel, String messageTimeStamp, String emojiCode);

    SlackMessageHandle<SlackChannelReply> setChannelTopic(Channel channel, String topic);

    SlackMessageHandle<SlackChannelReply> joinChannel(String channelName);

    SlackMessageHandle<SlackChannelReply> leaveChannel(Channel channel);

    SlackMessageHandle<SlackChannelReply> inviteToChannel(Channel channel, User user);

    SlackMessageHandle<ParsedSlackReply> archiveChannel(Channel channel);

    SlackMessageHandle<SlackChannelReply> openDirectMessageChannel(User user);

    SlackMessageHandle<SlackChannelReply> openMultipartyDirectMessageChannel(User... users);

    SlackMessageHandle<SlackMessageReply> sendTyping(Channel channel);

    SlackMessageHandle<GenericSlackReply> postGenericSlackCommand(Map<String, String> params, String command);

    /**
     *
     * @return true if actions is open
     */
    boolean isConnected();

    long getHeartbeat();

    void setHeartbeat(long heartbeat, TimeUnit unit);
}
