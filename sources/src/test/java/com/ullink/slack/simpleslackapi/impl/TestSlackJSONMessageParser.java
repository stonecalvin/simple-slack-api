package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.events.*;
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

    private static final String TEST_UNKNOWN_MESSAGE = "{\"type\":\"clearly_unknown\",\"channel\":\"TESTCHANNEL1\",\"user\":\"TESTUSER1\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000004\"}";
    private static final String TEST_NEW_MESSAGE = "{\"type\":\"message\",\"channel\":\"TESTCHANNEL1\",\"user\":\"TESTUSER1\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000004\"}";
    private static final String TEST_NEW_MESSAGE_FROM_INTEGRATION = "{\"type\":\"message\",\"channel\":\"TESTCHANNEL1\",\"bot_id\":\"TESTINTEGRATION1\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000004\"}";
    private static final String TEST_DELETED_MESSAGE = "{\"type\":\"message\",\"channel\":\"TESTCHANNEL1\",\"user\":\"TESTUSER1\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000005\", \"subtype\": \"message_deleted\", \"deleted_ts\": \"1358878749.000002\"}";
    private static final String TEST_UPDATED_MESSAGE = "{\"type\":\"message\",\"channel\":\"TESTCHANNEL1\",\"text\":\"Test text 1\",\"ts\":\"1358878755.001234\", \"subtype\": \"message_changed\", \"message\": {\"type:\" \"message\", \"user\": \"TESTUSER1\", \"text\": \"newtext\", \"ts\": \"1413187521.000005\", \"edited\": { \"user\": \"TESTUSER1\", \"ts\":\"1358878755.001234\"}}}";

    private static final String TEST_CHANNEL_CREATED = "{\"type\":\"channel_created\",\"channel\": { \"id\": \"NEWCHANNEL\", \"name\": \"new channel\", \"creator\": \"TESTUSER1\", \"topic\": {\"value\": \"Catz Wid Hatz\"}, \"purpose\": {\"value\": \"To post pictures of de Catz wid dem Hatz On\"}}}";
    private static final String TEST_CHANNEL_DELETED = "{\"type\":\"channel_deleted\",\"channel\": \"TESTCHANNEL1\"}";

    private static final String TEST_CHANNEL_ARCHIVED = "{\"type\":\"channel_archive\",\"channel\": \"TESTCHANNEL1\",\"user\":\"TESTUSER1\"}";
    private static final String TEST_CHANNEL_UNARCHIVED = "{\"type\":\"channel_unarchive\",\"channel\": \"TESTCHANNEL1\",\"user\":\"TESTUSER1\"}";

    private static final String NEW_CHANNEL = "\"channel\": { \"id\": \"NEWCHANNEL\", \"name\": \"new channel\", \"creator\": \"TESTUSER1\", \"topic\": {\"value\": \"To have something new\"}, \"purpose\": {\"value\": \"This channel so new it aint even old yet\"}}";
    private static final String TEST_GROUP_JOINED = "{\"type\":\"group_joined\"," + NEW_CHANNEL + "}";

    private static final String TEST_REACTION = " \"reaction\":\"thumbsup\", \"item\": {\"channel\":\"NEWCHANNEL\",\"ts\":\"1360782804.083113\"}";
    private static final String TEST_REACTION_ADDED = "{\"type\":\"reaction_added\", \"user\":\"TESTUSER1\", " + TEST_REACTION + "}";
    private static final String TEST_REACTION_REMOVED = "{\"type\":\"reaction_removed\", \"user\":\"TESTUSER1\", " + TEST_REACTION + "}";

    private static final String TEST_USER_CHANGE = "{\"type\": \"user_change\",\"user\": {\"id\": \"TESTUSER1\", \"name\": \"test user 1\"}}";

    private static final String TEST_ATTACHMENT = "{\"type\":\"message\",\"channel\":\"TESTCHANNEL1\",\"user\":\"TESTUSER1\",\"text\":\"Test text 1\",\"ts\":\"1413187521.000004\", \"attachments\": [{\"fallback\": \"Required plain-text summary of the attachment.\", \"color\": \"#36a64f\", \"pretext\": \"Optional text that appears above the attachment block\", \"author_name\": \"Bobby Tables\", \"author_link\": \"http://flickr.com/bobby/\", \"author_icon\": \"http://flickr.com/icons/bobby.jpg\", \"title\": \"Slack API Documentation\", \"title_link\": \"https://api.slack.com/\", \"text\": \"Optional text that appears within the attachment\", \"fields\": [ { \"title\": \"Priority\", \"value\": \"High\", \"short\": false } ], \"image_url\": \"http://my-website.com/path/to/image.jpg\", \"thumb_url\": \"http://example.com/path/to/thumb.png\", \"footer\": \"Slack API\", \"footer_icon\": \"https://platform.slack-edge.com/img/default_application_icon.png\", \"ts\": 123456789}]}";

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
            public void setPresence(SlackPersona.SlackPresence presence) {};

            @Override
            public void connect() {
                SlackUser user1 = new SlackUserImpl("TESTUSER1", "test user 1", "", "", "testSkype", "testPhone", "testTitle", false, false, false, false, false, false, false, "tz", "tzLabel", new Integer(0), SlackPersona.SlackPresence.ACTIVE);
                SlackUser user2 = new SlackUserImpl("TESTUSER2", "test user 2", "", "", "testSkype", "testPhone", "testTitle", false, false, false, false, false, false, false, "tz", "tzLabel", new Integer(0), SlackPersona.SlackPresence.ACTIVE);
                SlackUser user3 = new SlackUserImpl("TESTUSER3", "test user 3", "", "", "testSkype", "testPhone", "testTitle", false, false, false, false, false, false, false, "tz", "tzLabel", new Integer(0), SlackPersona.SlackPresence.ACTIVE);

                users.put(user1.getId(), user1);
                users.put(user2.getId(), user2);
                users.put(user3.getId(), user3);

                SlackIntegration integration = new SlackIntegrationImpl("TESTINTEGRATION1","integration 1",false);

                integrations.put(integration.getId(),integration);

                SlackChannel channel1 = new SlackChannelImpl("TESTCHANNEL1", "testchannel1", null, null, false, false);
                SlackChannel channel2 = new SlackChannelImpl("TESTCHANNEL2", "testchannel2", null, null, false, false);
                SlackChannel channel3 = new SlackChannelImpl("TESTCHANNEL3", "testchannel3", null, null, false, false);
                SlackChannel channel4 = new SlackChannelImpl("NEWCHANNEL", "new channel", "To have something new", "This channel so new it aint even old yet", false, false);
                channels.put(channel1.getId(), channel1);
                channels.put(channel2.getId(), channel2);
                channels.put(channel3.getId(), channel3);
                channels.put(channel4.getId(), channel4);
            }

            @Override
            public void disconnect() {
            }

            @Override
            public SlackMessageHandle sendMessageOverWebSocket(SlackChannel channel, String message) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SlackMessageHandle<SlackMessageReply> sendTyping(SlackChannel channel) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SlackPersona.SlackPresence getPresence(SlackPersona persona) {
                return null;
            }

            @Override
            public SlackMessageHandle deleteMessage(String timestamp, SlackChannel channel) {
                return null;
            }

            @Override
            public SlackMessageHandle<SlackMessageReply> sendMessage(SlackChannel channel, SlackPreparedMessage preparedMessage, SlackChatConfiguration chatConfiguration) {
                throw new UnsupportedOperationException();
            }

            @Override
            public SlackMessageHandle<SlackMessageReply> sendFile(SlackChannel channel, byte[] data, String fileName)
            {
                return null;
            }

            @Override
            public SlackMessageHandle updateMessage(String timestamp, SlackChannel channel, String message) {
                return null;
            }

            @Override
            public SlackMessageHandle addReactionToMessage(SlackChannel channel, String messageTimeStamp, String emojiCode) {
                return null;
            }

            @Override
            public SlackMessageHandle joinChannel(String channelName) {
                return null;
            }

            @Override
            public SlackMessageHandle<SlackChannelReply> setChannelTopic(SlackChannel channel, String topic) {
                return null;
            }

            @Override
            public SlackMessageHandle leaveChannel(SlackChannel channel) {
                return null;
            }

            @Override
            public SlackMessageHandle<SlackChannelReply> openDirectMessageChannel(SlackUser user)
            {
                return null;
            }

            @Override
            public SlackMessageHandle<SlackChannelReply> openMultipartyDirectMessageChannel(SlackUser... users)
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
            public SlackMessageHandle<SlackChannelReply> inviteToChannel(SlackChannel channel, SlackUser user) {
              return null;
            }

            @Override
            public SlackMessageHandle sendMessageToUser(SlackUser user, String message, SlackAttachment attachment) {
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
            public SlackMessageHandle<ParsedSlackReply> archiveChannel(SlackChannel channel)
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
        Assertions.assertThat(slackMessage.sender().getId()).isEqualTo("TESTUSER1");
        Assertions.assertThat(slackMessage.channel().getId()).isEqualTo("TESTCHANNEL1");
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
        Assertions.assertThat(slackMessage.sender().getId()).isEqualTo("TESTINTEGRATION1");
        Assertions.assertThat(slackMessage.channel().getId()).isEqualTo("TESTCHANNEL1");
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
        Assertions.assertThat(slackMessageDeleted.channel().getId()).isEqualTo("TESTCHANNEL1");
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
        Assertions.assertThat(slackMessageUpdated.channel().getId()).isEqualTo("TESTCHANNEL1");
        Assertions.assertThat(slackMessageUpdated.message()).isEqualTo("newtext");
    }

    @Test
    public void testChannelCreated() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_CHANNEL_CREATED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ChannelCreated.class);
        ChannelCreated slackChannelCreated = (ChannelCreated) event;
        Assertions.assertThat(slackChannelCreated.user().getId()).isEqualTo("TESTUSER1");
        Assertions.assertThat(slackChannelCreated.channel().getName()).isEqualTo("new channel");
        Assertions.assertThat(slackChannelCreated.channel().getId()).isEqualTo("NEWCHANNEL");
        Assertions.assertThat(slackChannelCreated.channel().getTopic()).isEqualTo("Catz Wid Hatz");
        Assertions.assertThat(slackChannelCreated.channel().getPurpose()).isEqualTo("To post pictures of de Catz wid dem Hatz On");
    }

    @Test
    public void testChannelDeleted() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_CHANNEL_DELETED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ChannelDeleted.class);
        ChannelDeleted slackChannelDeleted = (ChannelDeleted) event;
        Assertions.assertThat(slackChannelDeleted.channel().getId()).isEqualTo("TESTCHANNEL1");
    }

    @Test
    public void testChannelArchived() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_CHANNEL_ARCHIVED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ChannelArchived.class);
        ChannelArchived slackChannelArchived = (ChannelArchived) event;
        Assertions.assertThat(slackChannelArchived.channel().getId()).isEqualTo("TESTCHANNEL1");
        Assertions.assertThat(slackChannelArchived.user().getId()).isEqualTo("TESTUSER1");
    }

    @Test
    public void testChannelUnarchived() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_CHANNEL_UNARCHIVED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(ChannelUnarchived.class);
        ChannelUnarchived slackChannelUnarchived = (ChannelUnarchived) event;
        Assertions.assertThat(slackChannelUnarchived.channel().getId()).isEqualTo("TESTCHANNEL1");
        Assertions.assertThat(slackChannelUnarchived.user().getId()).isEqualTo("TESTUSER1");
    }

    @Test
    public void testGroupJoined() throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_GROUP_JOINED);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(GroupJoined.class);
        GroupJoined slackGroupJoined = (GroupJoined) event;
        Assertions.assertThat(slackGroupJoined.channel().getId()).isEqualTo("NEWCHANNEL");
        Assertions.assertThat(slackGroupJoined.channel().getName()).isEqualTo("new channel");
        Assertions.assertThat(slackGroupJoined.channel().getTopic()).isEqualTo("To have something new");
        Assertions.assertThat(slackGroupJoined.channel().getPurpose()).isEqualTo("This channel so new it aint even old yet");
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

    private void shouldValidateNewChannelsValues(SlackChannel channel) {
        Assert.assertTrue(channel.getId().equals("NEWCHANNEL"));
        Assert.assertTrue(channel.getName().equals("new channel"));
        Assert.assertTrue(channel.getPurpose().equals("This channel so new it aint even old yet"));
        Assert.assertTrue(channel.getTopic().equals("To have something new"));
    }

    @Test
    public void testUserChange() throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(TEST_USER_CHANGE);
        SlackEvent event = SlackJSONMessageParser.decode(session, object);
        Assertions.assertThat(event).isInstanceOf(UserChange.class);
        UserChange slackUserChange = (UserChange)event;
        SlackUser user = slackUserChange.user();
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getId()).isEqualTo("TESTUSER1");
        Assertions.assertThat(session.findUserById("TESTUSER1").getUserName()).isEqualTo(user.getUserName());
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
