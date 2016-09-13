package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.replies.*;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAbstractSlackSessionImpl
{
    private class TestSlackSessionImpl extends AbstractSlackSessionImpl
    {


        @Override
        public void setPresence(PresenceEnum presenceEnum) {
        }
        @Override
        public void connect()
        {
            channels.put("channelid1", TestUtils.generateChannel("1"));
            channels.put("channelid2", TestUtils.generateChannel("2"));
            channels.put("channelid3", TestUtils.generateChannel("3"));
            channels.put("channelid4", TestUtils.generateChannel("4"));
            channels.put("channelid5", TestUtils.generateChannel("5"));

            users.put("userid1", TestUtils.generateUser("1"));
            users.put("userid2", TestUtils.generateUser("2"));
            users.put("userid3", TestUtils.generateUser("3"));
            users.put("userid4", TestUtils.generateUser("4"));
            users.put("userid5", TestUtils.generateUser("5"));

            users.put("botid1", TestUtils.generateBot("1"));
            users.put("botid2", TestUtils.generateBot("2"));
            users.put("botid3", TestUtils.generateBot("3"));
        }

        @Override
        public void disconnect()
        {
        }

        @Override
        public SlackMessageHandle sendMessageOverWebSocket(SlackChannel channel, String message)
        {
            return null;
        }

        @Override
        public SlackMessageHandle<SlackParsedReply> sendTyping(SlackChannel channel) {
           return null;
        }

        @Override
        public PresenceEnum getPresence(SlackUser persona) {
            return null;
        }

        @Override
        public SlackMessageHandle deleteMessage(String timestamp, SlackChannel channel)
        {
            return null;
        }

        @Override
        public SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, SlackPreparedMessage preparedMessage, SlackChatConfiguration chatConfiguration) {
            return null;
        }

        @Override
        public SlackMessageHandle<SlackParsedReply> sendFile(SlackChannel channel, byte[] data, String fileName)
        {
            return null;
        }

        @Override
        public SlackMessageHandle updateMessage(String timestamp, SlackChannel channel, String message)
        {
            return null;
        }

        @Override
        public SlackMessageHandle addReactionToMessage(SlackChannel channel, String messageTimeStamp, String emojiCode)
        {
            return null;
        }

        @Override
        public SlackMessageHandle joinChannel(String channelName)
        {
            return null;
        }

        @Override
        public SlackMessageHandle<SlackParsedReply> setChannelTopic(SlackChannel channel, String topic) {
            return null;
        }

        @Override
        public SlackMessageHandle leaveChannel(SlackChannel channel)
        {
            return null;
        }

        @Override
        public SlackMessageHandle<SlackParsedReply> openDirectMessageChannel(SlackUser slackUser)
        {
            return null;
        }

        @Override
        public SlackMessageHandle<SlackParsedReply> openMultipartyDirectMessageChannel(SlackUser... slackUsers)
        {
            return null;
        }

        @Override
        public SlackMessageHandle<SlackParsedReply> listEmoji() {
            return null;
        }

        @Override
        public void refetchUsers() {}

        @Override
        public SlackMessageHandle inviteUser(String email, String firstName, boolean setActive)
        {
            return null;
        }

        @Override
        public boolean isConnected() {
            return true;
        }

        @Override
        public SlackMessageHandle<SlackParsedReply> inviteToChannel(SlackChannel channel, SlackUser slackUser) {
          return null;
        }

        @Override
        public SlackMessageHandle<SlackParsedReply> archiveChannel(SlackChannel channel)
        {
          return null;
        }

        @Override
        public SlackMessageHandle<GenericSlackReply> postGenericSlackCommand(java.util.Map<String,String> params, String command) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public SlackMessageHandle sendMessageToUser(SlackUser slackUser, String message, SlackAttachment attachment) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public SlackMessageHandle sendMessageToUser(String userName, String message, SlackAttachment attachment) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public long getHeartbeat() {
            return 0;
        }

        @Override
        public void setHeartbeat(long heartbeat, TimeUnit unit) {

        }
    }

    @Test
    public void testFindChannelByName_ExistingChannel()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        Optional<SlackChannel> channel = slackSession.findChannelByName("testchannel1");
        assertThat(channel.isPresent()).isTrue();
        assertThat(channel.get().getId()).isEqualTo("channelid1");
    }

    @Test
    public void testFindChannelByName_MissingChannel()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        assertThat(slackSession.findChannelByName("unknownChannel").isPresent()).isFalse();
    }

    @Test
    public void testFindChannelById_ExistingChannel()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        assertThat(slackSession.findChannelById("channelid1")).isNotNull();
        assertThat(slackSession.findChannelById("channelid1").getName().isPresent()).isTrue();
        assertThat(slackSession.findChannelById("channelid1").getName().get()).isEqualTo("testchannel1");
    }

    @Test
    public void testFindChannelById_MissingChannel()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        assertThat(slackSession.findChannelByName("unknownChannel").isPresent()).isFalse();
    }

    @Test
    public void testFindBotById_ExistingBot()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        assertThat(slackSession.findBotById("botid1")).isNotNull();
        assertThat(slackSession.findBotById("botid1").getName()).isEqualTo("botname1");
    }

    @Test
    public void testFindBotById_MissingBot()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        assertThat(slackSession.findBotById("unknownbot")).isNull();
    }

    @Test
    public void testFindUserById_ExistingBot()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        assertThat(slackSession.findUserById("userid1")).isNotNull();
        assertThat(slackSession.findUserById("userid1").getName()).isEqualTo("username1");
    }

    @Test
    public void testFindUserById_MissingBot()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        assertThat(slackSession.findBotById("unknownuser")).isNull();
    }

    @Test
    public void testFindUserByUserName_ExistingBot()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        assertThat(slackSession.findUserByUserName("username1").isPresent()).isTrue();
        assertThat(slackSession.findUserByUserName("username1").get().getId()).isEqualTo("userid1");
    }

    @Test
    public void testFindUserByUserName_MissingBot()
    {
        TestSlackSessionImpl slackSession = new TestSlackSessionImpl();

        slackSession.connect();

        assertThat(slackSession.findUserByUserName("unknownuser").isPresent()).isFalse();
    }
}
