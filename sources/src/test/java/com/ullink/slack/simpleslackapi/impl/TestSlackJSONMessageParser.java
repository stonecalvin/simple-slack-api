package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.events.*;
import com.ullink.slack.simpleslackapi.json.*;
import com.ullink.slack.simpleslackapi.replies.*;
import org.assertj.core.api.Assertions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TestSlackJSONMessageParser {

    SlackSession session;

    private static final String USER_1_ID = "TESTUSER1";
    private static final String USER_2_ID = "TESTUSER2";
    private static final String USER_3_ID = "TESTUSER3";

    private static final String CHANNEL_1_ID = "TESTCHANNEL1";
    private static final String CHANNEL_2_ID = "TESTCHANNEL2";
    private static final String CHANNEL_3_ID = "TESTCHANNEL3";


    private static final String TEST_UNKNOWN_MESSAGE = "{\"type\":\"clearly_unknown\",\"channel\":\"" + CHANNEL_1_ID + "\",\"user\":\"" + USER_1_ID + "\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000004\"}";
    private static final String TEST_NEW_MESSAGE = "{\"type\":\"message\",\"channel\":\"" + CHANNEL_1_ID + "\",\"user\":\"" + USER_1_ID + "\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000004\"}";
    private static final String TEST_NEW_MESSAGE_FROM_INTEGRATION = "{\"type\":\"message\",\"channel\":\"" + CHANNEL_1_ID + "\",\"bot_id\":\"TESTINTEGRATION1\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000004\"}";
    private static final String TEST_DELETED_MESSAGE = "{\"type\":\"message\",\"channel\":\"" + CHANNEL_1_ID + "\",\"user\":\"" + USER_1_ID + "\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000005\", \"subtype\": \"message_deleted\", \"deleted_ts\": \"1358878749.000002\"}";
    private static final String TEST_UPDATED_MESSAGE = "{\"type\":\"message\",\"channel\":\"" + CHANNEL_1_ID + "\",\"text\":\"Test text 1\",\"ts\":\"1358878755.001234\", \"subtype\": \"message_changed\", \"message\": {\"type:\" \"message\", \"user\": \"" + USER_1_ID + "\", \"text\": \"newtext\", \"ts\": \"1413187521.000005\", \"edited\": { \"user\": \"" + USER_1_ID + "\", \"ts\":\"1358878755.001234\"}}}";

    private static final String TEST_CHANNEL_CREATED = "{\"type\":\"channel_created\",\"channel\": { \"id\": \"NEWCHANNEL\", \"name\": \"new channel\", \"creator\": \"" + USER_1_ID + "\", \"topic\": {\"value\": \"Catz Wid Hatz\"}, \"purpose\": {\"value\": \"To post pictures of de Catz wid dem Hatz On\"}}}";
    private static final String TEST_CHANNEL_DELETED = "{\"type\":\"channel_deleted\",\"channel\": \"" + CHANNEL_1_ID + "\"}";

    private static final String TEST_CHANNEL_ARCHIVED = "{\"type\":\"channel_archive\",\"channel\": \"" + CHANNEL_1_ID + "\",\"user\":\"" + USER_1_ID + "\"}";
    private static final String TEST_CHANNEL_UNARCHIVED = "{\"type\":\"channel_unarchive\",\"channel\": \"" + CHANNEL_1_ID + "\",\"user\":\"" + USER_1_ID + "\"}";

    private static final String NEW_CHANNEL = "\"channel\": { \"id\": \"NEWCHANNEL\", \"name\": \"new channel\", \"creator\": \"" + USER_1_ID + "\", \"topic\": {\"value\": \"To have something new\"}, \"purpose\": {\"value\": \"This channel so new it aint even old yet\"}}";
    private static final String TEST_GROUP_JOINED = "{\"type\":\"group_joined\"," + NEW_CHANNEL + "}";

    private static final String TEST_REACTION = " \"reaction\":\"thumbsup\", \"item\": {\"channel\":\"NEWCHANNEL\",\"ts\":\"1360782804.083113\"}";
    private static final String TEST_REACTION_ADDED = "{\"type\":\"reaction_added\", \"user\":\"" + USER_1_ID + "\", " + TEST_REACTION + "}";
    private static final String TEST_REACTION_REMOVED = "{\"type\":\"reaction_removed\", \"user\":\"" + USER_1_ID + "\", " + TEST_REACTION + "}";

    private static final String TEST_USER_CHANGE = "{\"type\": \"user_change\",\"user\": {\"id\": \"" + USER_1_ID + "\", \"name\": \"test user 1\"}}";

    private static final String TEST_ATTACHMENT = "{\"type\":\"message\",\"channel\":\"" + CHANNEL_1_ID + "\",\"user\":\"" + USER_1_ID + "\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000004\", \"attachments\": [{\"fallback\": \"Required plain-text summary of the attachment.\", \"color\": \"#36a64f\", \"pretext\": \"Optional text that appears above the attachment block\", \"author_name\": \"Bobby Tables\", \"author_link\": \"http://flickr.com/bobby/\", \"author_icon\": \"http://flickr.com/icons/bobby.jpg\", \"title\": \"Slack API Documentation\", \"title_link\": \"https://api.slack.com/\", \"text\": \"Optional text that appears within the attachment\", \"fields\": [ { \"title\": \"Priority\", \"value\": \"High\", \"short\": false } ], \"image_url\": \"http://my-website.com/path/to/image.jpg\", \"thumb_url\": \"http://example.com/path/to/thumb.png\", \"footer\": \"Slack API\", \"footer_icon\": \"https://platform.slack-edge.com/img/default_application_icon.png\", \"ts\": 123456789}]}";

    @Before
    public void setup() {
        session = new AbstractSlackSessionImpl() {

            @Override
            public long getHeartbeat() {
                return 0;
            }

            @Override
            public void setHeartbeat(long heartbeat, TimeUnit unit) {

            }

            @Override
            public void setPresence(Presence presence) {};

            @Override
            public void connect() {
                User user1 = ImmutableUser.copyOf(TestUtils.generateUser("1")).withId(USER_1_ID).withName("test user 1");
                User user2 = ImmutableUser.copyOf(TestUtils.generateUser("2")).withId(USER_2_ID).withName("test user 2");
                User user3 = ImmutableUser.copyOf(TestUtils.generateUser("3")).withId(USER_3_ID).withName("test user 3");

                users.put(user1.id(), user1);
                users.put(user2.id(), user2);
                users.put(user3.id(), user3);

                Integration integration = ImmutableIntegration.builder()
                        .id("TESTINTEGRATION1")
                        .name("integration 1")
                        .build();

                integrations.put(integration.id(), integration);

                Channel channel1 = ImmutableChannel.copyOf(TestUtils.generateChannel("1")).withId(CHANNEL_1_ID).withName("testchannel1");
                Channel channel2 = ImmutableChannel.copyOf(TestUtils.generateChannel("2")).withId(CHANNEL_2_ID).withName("testchannel2");
                Channel channel3 = ImmutableChannel.copyOf(TestUtils.generateChannel("3")).withId(CHANNEL_3_ID).withName("testchannel3");
                Channel channel4 = ImmutableChannel.builder()
                        .id("NEWCHANNEL")
                        .name("new channel")
                        .topic("To have something new")
                        .purpose("This channel so new it aint even old yet")
                        .build();

                channels.put(channel1.id(), channel1);
                channels.put(channel2.id(), channel2);
                channels.put(channel3.id(), channel3);
                channels.put(channel4.id(), channel4);
            }

            @Override
            public void disconnect() {
            }

            @Override
            public SlackMessageHandle sendMessageOverWebSocket(Channel channel, String message) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SlackMessageHandle<SlackMessageReply> sendTyping(Channel channel) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Presence getPresence(User persona) {
                return null;
            }

            @Override
            public SlackMessageHandle deleteMessage(String timestamp, Channel channel) {
                return null;
            }

            @Override
            public SlackMessageHandle<SlackMessageReply> sendMessage(Channel channel, SlackPreparedMessage preparedMessage, SlackChatConfiguration chatConfiguration) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SlackMessageHandle<SlackMessageReply> sendFile(Channel channel, byte[] data, String fileName)
            {
                return null;
            }

            @Override
            public SlackMessageHandle updateMessage(String timestamp, Channel channel, String message) {
                return null;
            }

            @Override
            public SlackMessageHandle addReactionToMessage(Channel channel, String messageTimeStamp, String emojiCode) {
                return null;
            }

            @Override
            public SlackMessageHandle joinChannel(String channelName) {
                return null;
            }

            @Override
            public SlackMessageHandle<SlackChannelReply> setChannelTopic(Channel channel, String topic) {
                return null;
            }

            @Override
            public SlackMessageHandle leaveChannel(Channel channel) {
                return null;
            }

            @Override
            public SlackMessageHandle<SlackChannelReply> openDirectMessageChannel(User user)
            {
                return null;
            }

            @Override
            public SlackMessageHandle<SlackChannelReply> openMultipartyDirectMessageChannel(User... users)
            {
                return null;
            }

            @Override
            public SlackMessageHandle<EmojiSlackReply> listEmoji() {
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
            public SlackMessageHandle<SlackChannelReply> inviteToChannel(Channel channel, User user) {
              return null;
            }

            @Override
            public SlackMessageHandle sendMessageToUser(User user, String message, SlackAttachment attachment) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public SlackMessageHandle sendMessageToUser(String userName, String message, SlackAttachment attachment) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public SlackMessageHandle<GenericSlackReply> postGenericSlackCommand(Map<String, String> params, String command)
            {
                return null;
            }

            @Override
            public SlackMessageHandle<ParsedSlackReply> archiveChannel(Channel channel)
            {
                return null;
            }

        };
        try {
            session.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testParsingUnknownMessage() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_UNKNOWN_MESSAGE);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(UnknownEvent.class);
    }

    @Test
    public void testParsingNewMessage() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_NEW_MESSAGE);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(MessagePosted.class);
        MessagePosted slackMessage = (MessagePosted) event;
        Assertions.assertThat(slackMessage.sender().id()).isEqualTo(USER_1_ID);
        Assertions.assertThat(slackMessage.channel().id()).isEqualTo(CHANNEL_1_ID);
        Assertions.assertThat(slackMessage.messageContent()).isEqualTo("Test text 1");
        Assertions.assertThat(slackMessage.timestamp()).isEqualTo("1413187521.000004");
    }

    @Test
    public void testParsingNewMessageFromIntegration() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_NEW_MESSAGE_FROM_INTEGRATION);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(MessagePosted.class);
        MessagePosted slackMessage = (MessagePosted) event;
        Assertions.assertThat(slackMessage.sender().id()).isEqualTo("TESTINTEGRATION1");
        Assertions.assertThat(slackMessage.channel().id()).isEqualTo(CHANNEL_1_ID);
        Assertions.assertThat(slackMessage.messageContent()).isEqualTo("Test text 1");
        Assertions.assertThat(slackMessage.timestamp()).isEqualTo("1413187521.000004");
    }

    @Test
    public void testParsingMessageDeleted() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_DELETED_MESSAGE);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(MessageDeleted.class);
        MessageDeleted slackMessageDeleted = (MessageDeleted) event;
        Assertions.assertThat(slackMessageDeleted.deletedTimestamp()).isEqualTo("1358878749.000002");
        Assertions.assertThat(slackMessageDeleted.timestamp()).isEqualTo("1413187521.000005");
        Assertions.assertThat(slackMessageDeleted.channel().id()).isEqualTo(CHANNEL_1_ID);
    }

    @Test
    public void testParsingMessageChanged() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_UPDATED_MESSAGE);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(MessageUpdated.class);
        MessageUpdated slackMessageUpdated = (MessageUpdated) event;
        Assertions.assertThat(slackMessageUpdated.messageTimestamp()).isEqualTo("1413187521.000005");
        Assertions.assertThat(slackMessageUpdated.timestamp()).isEqualTo("1358878755.001234");
        Assertions.assertThat(slackMessageUpdated.channel().id()).isEqualTo(CHANNEL_1_ID);
        Assertions.assertThat(slackMessageUpdated.message()).isEqualTo("newtext");
    }

    @Test
    public void testChannelCreated() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_CHANNEL_CREATED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ChannelCreated.class);
        ChannelCreated slackChannelCreated = (ChannelCreated) event;
        Assertions.assertThat(slackChannelCreated.user().id()).isEqualTo(USER_1_ID);

        Assertions.assertThat(slackChannelCreated.channel().id()).isEqualTo("NEWCHANNEL");

        Assertions.assertThat(slackChannelCreated.channel().name().isPresent()).isTrue();
        Assertions.assertThat(slackChannelCreated.channel().topic().isPresent()).isTrue();
        Assertions.assertThat(slackChannelCreated.channel().purpose().isPresent()).isTrue();

        Assertions.assertThat(slackChannelCreated.channel().name().get()).isEqualTo("new channel");
        Assertions.assertThat(slackChannelCreated.channel().topic().get()).isEqualTo("Catz Wid Hatz");
        Assertions.assertThat(slackChannelCreated.channel().purpose().get()).isEqualTo("To post pictures of de Catz wid dem Hatz On");
    }

    @Test
    public void testChannelDeleted() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_CHANNEL_DELETED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ChannelDeleted.class);
        ChannelDeleted slackChannelDeleted = (ChannelDeleted) event;
        Assertions.assertThat(slackChannelDeleted.channel().id()).isEqualTo(CHANNEL_1_ID);
    }

    @Test
    public void testChannelArchived() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_CHANNEL_ARCHIVED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ChannelArchived.class);
        ChannelArchived slackChannelArchived = (ChannelArchived) event;
        Assertions.assertThat(slackChannelArchived.channel().id()).isEqualTo(CHANNEL_1_ID);
        Assertions.assertThat(slackChannelArchived.user().id()).isEqualTo(USER_1_ID);
    }

    @Test
    public void testChannelUnarchived() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_CHANNEL_UNARCHIVED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ChannelUnarchived.class);
        ChannelUnarchived slackChannelUnarchived = (ChannelUnarchived) event;
        Assertions.assertThat(slackChannelUnarchived.channel().id()).isEqualTo(CHANNEL_1_ID);
        Assertions.assertThat(slackChannelUnarchived.user().id()).isEqualTo(USER_1_ID);
    }

    @Test
    public void testGroupJoined() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_GROUP_JOINED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(GroupJoined.class);
        GroupJoined slackGroupJoined = (GroupJoined) event;
        Assertions.assertThat(slackGroupJoined.channel().id()).isEqualTo("NEWCHANNEL");

        Assertions.assertThat(slackGroupJoined.channel().name().isPresent()).isTrue();
        Assertions.assertThat(slackGroupJoined.channel().topic().isPresent()).isTrue();
        Assertions.assertThat(slackGroupJoined.channel().purpose().isPresent()).isTrue();

        Assertions.assertThat(slackGroupJoined.channel().name().get()).isEqualTo("new channel");
        Assertions.assertThat(slackGroupJoined.channel().topic().get()).isEqualTo("To have something new");
        Assertions.assertThat(slackGroupJoined.channel().purpose().get()).isEqualTo("This channel so new it aint even old yet");
    }

    @Test
    public void shouldParseReactionAddedEvent() throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_REACTION_ADDED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ReactionAdded.class);
        ReactionAdded reacAdded = (ReactionAdded) event;
        Assert.assertTrue(reacAdded.emojiName().equals("thumbsup"));
        Assert.assertTrue(reacAdded.messageID().equals("1360782804.083113"));
        shouldValidateNewChannelsValues(reacAdded.channel());
    }

    @Test
    public void shouldParseReactionRemovedEvent() throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_REACTION_REMOVED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ReactionRemoved.class);
        ReactionRemoved reacRemoved = (ReactionRemoved) event;
        Assert.assertTrue(reacRemoved.emojiName().equals("thumbsup"));
        Assert.assertTrue(reacRemoved.messageID().equals("1360782804.083113"));
        shouldValidateNewChannelsValues(reacRemoved.channel());

    }

    private void shouldValidateNewChannelsValues(Channel channel) {
        Assert.assertTrue(channel.id().equals("NEWCHANNEL"));

        Assert.assertTrue(channel.name().isPresent());
        Assert.assertTrue(channel.purpose().isPresent());
        Assert.assertTrue(channel.topic().isPresent());

        Assert.assertTrue(channel.name().get().equals("new channel"));
        Assert.assertTrue(channel.purpose().get().equals("This channel so new it aint even old yet"));
        Assert.assertTrue(channel.topic().get().equals("To have something new"));
    }

    @Test
    public void testUserChange() throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_USER_CHANGE);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(UserChange.class);
        UserChange slackUserChange = (UserChange)event;
        User user = slackUserChange.user();
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.id()).isEqualTo(USER_1_ID);
        Assertions.assertThat(session.findUserById(USER_1_ID).name()).isEqualTo(user.name());
    }

    @Test
    public void testAttachment() throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_ATTACHMENT);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(MessagePosted.class);
        MessagePosted slackMessage = (MessagePosted) event;
        Assertions.assertThat(slackMessage.attachments().isPresent()).isTrue();
        Assertions.assertThat(slackMessage.attachments().get().size() == 1);

        SlackAttachment attachment = slackMessage.attachments().get().get(0);

        Assertions.assertThat(attachment.getFallback()).isEqualTo("Required plain-text summary of the attachment.");
        Assertions.assertThat(attachment.getColor()).isEqualTo("#36a64f");
        Assertions.assertThat(attachment.getPretext()).isEqualTo("Optional text that appears above the attachment block");
        Assertions.assertThat(attachment.getAuthorName()).isEqualTo("Bobby Tables");
        Assertions.assertThat(attachment.getAuthorLink()).isEqualTo("http://flickr.com/bobby/");
        Assertions.assertThat(attachment.getAuthorIcon()).isEqualTo("http://flickr.com/icons/bobby.jpg");
        Assertions.assertThat(attachment.getTitle()).isEqualTo("Slack API Documentation");
        Assertions.assertThat(attachment.getTitleLink()).isEqualTo("https://api.slack.com/");
        Assertions.assertThat(attachment.getText()).isEqualTo("Optional text that appears within the attachment");
        Assertions.assertThat(attachment.getThumbUrl()).isEqualTo("http://example.com/path/to/thumb.png");
        Assertions.assertThat(attachment.getFooter()).isEqualTo("Slack API");
        Assertions.assertThat(attachment.getFooterIcon()).isEqualTo("https://platform.slack-edge.com/img/default_application_icon.png");

        Assertions.assertThat(attachment.getFields().size()).isEqualTo(1);

        SlackField field = attachment.getFields().get(0);

        Assertions.assertThat(field.getTitle()).isEqualTo("Priority");
        Assertions.assertThat(field.getValue()).isEqualTo("High");
        Assertions.assertThat(field.isShort()).isEqualTo(false);
    }
}
