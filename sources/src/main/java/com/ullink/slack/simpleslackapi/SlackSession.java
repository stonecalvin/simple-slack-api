package com.ullink.slack.simpleslackapi;

import com.google.gson.Gson;
import com.ullink.slack.simpleslackapi.impl.SlackChatConfiguration;
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

    SlackTeam getSlackTeam();

    Collection<SlackChannel> getChannels();

    Collection<SlackUser> getUsers();

    Collection<SlackIntegration> getIntegrations();

    Optional<SlackChannel> findChannelByName(String channelName);

    SlackChannel findChannelById(String channelId);

    SlackIntegration findIntegrationById(String integrationId);

    SlackUser findUserById(String userId);

    SlackUser findBotById(String botId);

    Optional<SlackUser> findUserByUserName(String userName);

    Optional<SlackUser> findUserByEmail(String userMail);

    SlackUser sessionPersona();

    SlackMessageHandle<SlackParsedReply> listEmoji();

    void refetchUsers();

    PresenceEnum getPresence(SlackUser persona);

    void setPresence(PresenceEnum presenceEnum);

    void connect() throws IOException;

    void disconnect() throws IOException;

    SlackMessageHandle<SlackParsedReply> inviteUser(String email, String firstName, boolean setActive);

    SlackMessageHandle<SlackParsedReply> deleteMessage(String timestamp, SlackChannel channel);

    SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, SlackPreparedMessage preparedMessage, SlackChatConfiguration chatConfiguration);

    SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, SlackPreparedMessage preparedMessage);

    SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration, boolean unfurl);

    SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment, SlackChatConfiguration chatConfiguration);

    SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment, boolean unfurl);

    SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, SlackAttachment attachment);

    SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message, boolean unfurl);

    SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, String message);

    SlackMessageHandle<SlackParsedReply> sendFile(SlackChannel channel, byte [] data, String fileName);

    SlackMessageHandle<SlackParsedReply> sendMessageToUser(SlackUser slackUser, String message, SlackAttachment attachment);

    SlackMessageHandle<SlackParsedReply> sendMessageToUser(String userName, String message, SlackAttachment attachment);

    SlackMessageHandle<SlackParsedReply> updateMessage(String timestamp, SlackChannel channel, String message);

    SlackMessageHandle<SlackParsedReply> sendMessageOverWebSocket(SlackChannel channel, String message);

    SlackMessageHandle<SlackParsedReply> addReactionToMessage(SlackChannel channel, String messageTimeStamp, String emojiCode);

    SlackMessageHandle<SlackParsedReply> setChannelTopic(SlackChannel channel, String topic);

    SlackMessageHandle<SlackParsedReply> joinChannel(String channelName);

    SlackMessageHandle<SlackParsedReply> leaveChannel(SlackChannel channel);

    SlackMessageHandle<SlackParsedReply> inviteToChannel(SlackChannel channel, SlackUser slackUser);

    SlackMessageHandle<SlackParsedReply> archiveChannel(SlackChannel channel);

    SlackMessageHandle<SlackParsedReply> openDirectMessageChannel(SlackUser slackUser);

    SlackMessageHandle<SlackParsedReply> openMultipartyDirectMessageChannel(SlackUser... slackUsers);

    SlackMessageHandle<SlackParsedReply> sendTyping(SlackChannel channel);

    SlackMessageHandle<GenericSlackReply> postGenericSlackCommand(Map<String, String> params, String command);

    /**
     *
     * @return true if actions is open
     */
    boolean isConnected();

    long getHeartbeat();

    void setHeartbeat(long heartbeat, TimeUnit unit);
}
