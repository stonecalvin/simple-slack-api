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

    SlackMessageHandle<MyParsedReply> listEmoji();

    void refetchUsers();

    Presence getPresence(User persona);

    void setPresence(Presence presence);

    void connect() throws IOException;

    void disconnect() throws IOException;

    SlackMessageHandle<MyParsedReply> inviteUser(String email, String firstName, boolean setActive);

    SlackMessageHandle<MyParsedReply> deleteMessage(String timestamp, Channel channel);

    SlackMessageHandle<MyParsedReply> sendMessage(Channel channel, MyPreparedMessage preparedMessage, SlackChatConfiguration chatConfiguration);

    SlackMessageHandle<MyParsedReply> sendMessage(Channel channel, MyPreparedMessage preparedMessage);

    SlackMessageHandle<MyParsedReply> sendMessage(Channel channel, String message, MyAttachment attachment, SlackChatConfiguration chatConfiguration, boolean unfurl);

    SlackMessageHandle<MyParsedReply> sendMessage(Channel channel, String message, MyAttachment attachment, SlackChatConfiguration chatConfiguration);

    SlackMessageHandle<MyParsedReply> sendMessage(Channel channel, String message, MyAttachment attachment, boolean unfurl);

    SlackMessageHandle<MyParsedReply> sendMessage(Channel channel, String message, MyAttachment attachment);

    SlackMessageHandle<MyParsedReply> sendMessage(Channel channel, String message, boolean unfurl);

    SlackMessageHandle<MyParsedReply> sendMessage(Channel channel, String message);

    SlackMessageHandle<MyParsedReply> sendFile(Channel channel, byte [] data, String fileName);

    SlackMessageHandle<MyParsedReply> sendMessageToUser(User user, String message, MyAttachment attachment);

    SlackMessageHandle<MyParsedReply> sendMessageToUser(String userName, String message, MyAttachment attachment);

    SlackMessageHandle<MyParsedReply> updateMessage(String timestamp, Channel channel, String message);

    SlackMessageHandle<MyParsedReply> sendMessageOverWebSocket(Channel channel, String message);

    SlackMessageHandle<MyParsedReply> addReactionToMessage(Channel channel, String messageTimeStamp, String emojiCode);

    SlackMessageHandle<MyParsedReply> setChannelTopic(Channel channel, String topic);

    SlackMessageHandle<MyParsedReply> joinChannel(String channelName);

    SlackMessageHandle<MyParsedReply> leaveChannel(Channel channel);

    SlackMessageHandle<MyParsedReply> inviteToChannel(Channel channel, User user);

    SlackMessageHandle<MyParsedReply> archiveChannel(Channel channel);

    SlackMessageHandle<MyParsedReply> openDirectMessageChannel(User user);

    SlackMessageHandle<MyParsedReply> openMultipartyDirectMessageChannel(User... users);

    SlackMessageHandle<MyParsedReply> sendTyping(Channel channel);

    SlackMessageHandle<GenericSlackReply> postGenericSlackCommand(Map<String, String> params, String command);

    /**
     *
     * @return true if actions is open
     */
    boolean isConnected();

    long getHeartbeat();

    void setHeartbeat(long heartbeat, TimeUnit unit);
}
