package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.events.*;
import com.ullink.slack.simpleslackapi.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

class SlackJSONMessageParser {

    static SlackEvent decode(SlackSession slackSession, JSONObject obj) {
        String type = (String) obj.get("type");
        if (type == null) {
            return ImmutableUnknownEvent.builder().build();
        }

        EventType eventType = EventType.getByCode(type);
        switch (eventType) {
            case MESSAGE:
                return extractMessageEvent(slackSession, obj);
            case CHANNEL_CREATED:
                return extractChannelCreatedEvent(slackSession, obj);
            case CHANNEL_ARCHIVE:
                return extractChannelArchiveEvent(slackSession, obj);
            case CHANNEL_DELETED:
                return extractChannelDeletedEvent(slackSession, obj);
            case CHANNEL_RENAME:
                return extractChannelRenamedEvent(slackSession, obj);
            case CHANNEL_UNARCHIVE:
                return extractChannelUnarchiveEvent(slackSession, obj);
            case CHANNEL_JOINED:
                return extractChannelJoinedEvent(slackSession, obj);
            case CHANNEL_LEFT:
                return extractChannelLeftEvent(slackSession, obj);
            case GROUP_JOINED:
                return extractGroupJoinedEvent(slackSession, obj);
            case REACTION_ADDED:
                return extractReactionAddedEvent(slackSession, obj);
            case REACTION_REMOVED:
                return extractReactionRemovedEvent(slackSession, obj);
            case USER_CHANGE:
                return extractUserChangeEvent(slackSession, obj);
            case PRESENCE_CHANGE:
                return extractPresenceChangeEvent(slackSession, obj);
            case PIN_ADDED:
                return extractPinAddedEvent(slackSession, obj);
            case PIN_REMOVED:
                return extractPinRemovedEvent(slackSession, obj);
            default:
                return ImmutableUnknownEvent.builder().slackSession(slackSession).build();
        }
    }

    private static ChannelJoined extractChannelJoinedEvent(SlackSession slackSession, JSONObject obj)
    {
        JSONObject channelJSONObject = (JSONObject) obj.get("channel");
        Channel slackChannel = parseChannelDescription(channelJSONObject);

        return ImmutableChannelJoined.builder()
                .slackSession(slackSession)
                .channel(slackChannel)
                .build();
    }

    private static ChannelLeft extractChannelLeftEvent(SlackSession slackSession, JSONObject obj)
    {
        String channelId = (String) obj.get("channel");
        Channel slackChannel = slackSession.findChannelById(channelId);
        return ImmutableChannelLeft.builder()
                .slackSession(slackSession)
                .channelId(slackChannel.id())
                .build();
    }

    private static GroupJoined extractGroupJoinedEvent(SlackSession slackSession, JSONObject obj)
    {
        JSONObject channelJSONObject = (JSONObject) obj.get("channel");
        Channel slackChannel = parseChannelDescription(channelJSONObject);
        return ImmutableGroupJoined.builder()
                .slackSession(slackSession)
                .channel(slackChannel)
                .build();
    }

    private static ChannelRenamed extractChannelRenamedEvent(SlackSession slackSession, JSONObject obj)
    {
        String channelId = (String) obj.get("channel");
        String newName = (String) obj.get("name");
        return ImmutableChannelRenamed.builder()
                .slackSession(slackSession)
                .channel(slackSession.findChannelById(channelId))
                .newName(newName)
                .build();
    }

    private static ChannelDeleted extractChannelDeletedEvent(SlackSession slackSession, JSONObject obj)
    {
        String channelId = (String) obj.get("channel");
        return ImmutableChannelDeleted.builder()
                .slackSession(slackSession)
                .channel(slackSession.findChannelById(channelId))
                .build();
    }

    private static ChannelUnarchived extractChannelUnarchiveEvent(SlackSession slackSession, JSONObject obj)
    {
        String channelId = (String) obj.get("channel");
        String userId = (String) obj.get("user");
        return ImmutableChannelUnarchived.builder()
                .slackSession(slackSession)
                .channel(slackSession.findChannelById(channelId))
                .user(slackSession.findUserById(userId))
                .build();
    }

    private static ChannelArchived extractChannelArchiveEvent(SlackSession slackSession, JSONObject obj)
    {
        String channelId = (String) obj.get("channel");
        String userId = (String) obj.get("user");
        return ImmutableChannelArchived.builder()
                .slackSession(slackSession)
                .channel(slackSession.findChannelById(channelId))
                .user(slackSession.findUserById(userId))
                .build();
    }

    private static ChannelCreated extractChannelCreatedEvent(SlackSession slackSession, JSONObject obj)
    {
        JSONObject channelJSONObject = (JSONObject) obj.get("channel");
        Channel channel = parseChannelDescription(channelJSONObject);
        String creatorId = (String) channelJSONObject.get("creator");
        User user = slackSession.findUserById(creatorId);
        return ImmutableChannelCreated.builder()
                .slackSession(slackSession)
                .channel(channel)
                .user(user)
                .build();
    }

    private static SlackEvent extractMessageEvent(SlackSession slackSession, JSONObject obj)
    {
        String channelId = (String) obj.get("channel");
        Channel channel = getChannel(slackSession, channelId);

        String ts = (String) obj.get("ts");
        MessageSubtype subType = MessageSubtype.fromCode((String) obj.get("subtype"));
        switch (subType)
        {
            case MESSAGE_CHANGED:
                return parseMessageUpdated(slackSession, obj, channel, ts);
            case MESSAGE_DELETED:
                return parseMessageDeleted(slackSession, obj, channel, ts);
            case FILE_SHARE:
                return parseMessagePublishedWithFile(obj, channel, ts, slackSession);
            default:
                return parseMessagePublished(obj, channel, ts, slackSession);
        }
    }

    private static Channel getChannel(SlackSession slackSession, String channelId)
    {
        if (channelId != null)
        {
            if (channelId.startsWith("D"))
            {
                // direct messaging, on the fly channel creation
                return ImmutableChannel.builder()
                        .id(channelId)
                        .name(channelId)
                        .isIm(true)
                        .build();
            }
            else
            {
                return slackSession.findChannelById(channelId);
            }
        }
        return null;
    }

    private static MessageUpdated parseMessageUpdated(SlackSession slackSession, JSONObject obj, Channel channel, String ts)
    {
        JSONObject message = (JSONObject) obj.get("message");
        String text = (String) message.get("text");
        String messageTs = (String) message.get("ts");
        List<SlackAttachment> attachments = extractAttachmentsFromMessageJSON(message);

        return ImmutableMessageUpdated.builder()
                .slackSession(slackSession)
                .channelId(channel.id())
                .ts(ts)
                .messageTimestamp(messageTs)
                .message(ImmutableMessage.builder().text(text).user("test").ts(messageTs).build())
//                .attachments(Optional.ofNullable(attachments))
                .build();
    }

    private static MessageDeleted parseMessageDeleted(SlackSession slackSession, JSONObject obj, Channel channel, String ts)
    {
        String deletedTs = (String) obj.get("deleted_ts");

        return ImmutableMessageDeleted.builder()
                .slackSession(slackSession)
                .channelId(channel.id())
                .ts(ts)
                .deletedTimestamp(deletedTs)
                .build();
    }

    private static MessagePosted parseMessagePublished(JSONObject obj, Channel channel, String ts, SlackSession slackSession) {
        String text = (String) obj.get("text");
        String userId = (String) obj.get("user");
        if (userId == null) {
            userId = (String) obj.get("bot_id");
        }
        String subtype = (String) obj.get("subtype");
        User user = slackSession.findUserById(userId);
        if (user == null) {

            Integration integration = slackSession.findIntegrationById(userId);
            if (integration == null) {
                throw new IllegalStateException("unknown user id: " + userId);
            }
            user = ImmutableUser.builder()
                    .id(integration.id())
                    .name(integration.name())
                    .deleted(integration.deleted())
                    .build();

        }
        Map<String, Integer> reacs = extractReactionsFromMessageJSON(obj);
        ArrayList<SlackAttachment> attachments = extractAttachmentsFromMessageJSON(obj);

        return ImmutableMessagePosted.builder()
                .slackSession(slackSession)
                .channelId(channel.id())
                .ts(ts)
                .text(text)
                .userId(user.id())
                .jsonSource(obj)
//                .attachments(Optional.ofNullable(attachments))
                .reactions(Optional.ofNullable(reacs))
                .build();

    }

    private final static String COMMENT_PLACEHOLDER = "> and commented:";


     private static void parseSlackFileFromRaw(JSONObject rawFile, SlackFile file) {
        file.setId((String) rawFile.get("id"));
        file.setName((String) rawFile.get("name"));
        file.setTitle((String) rawFile.get("title"));
        file.setMimetype((String) rawFile.get("mimetype"));
        file.setFiletype((String) rawFile.get("filetype"));
        file.setUrl((String) rawFile.get("url"));
        file.setUrlDownload((String) rawFile.get("url_download"));
        file.setUrlPrivate((String) rawFile.get("url_private"));
        file.setUrlPrivateDownload((String) rawFile.get("url_private_download"));
        file.setThumb64((String) rawFile.get("thumb_64"));
        file.setThumb80((String) rawFile.get("thumb_80"));
        file.setThumb160((String) rawFile.get("thumb_160"));
        file.setThumb360((String) rawFile.get("thumb_360"));
        file.setThumb480((String) rawFile.get("thumb_480"));
        file.setThumb720((String) rawFile.get("thumb_720"));
        try{
            file.setOriginalH((Long) rawFile.get("original_h"));
            file.setOriginalW((Long) rawFile.get("original_w"));
            file.setImageExifRotation((Long) rawFile.get("image_exif_rotation"));
        } catch(Exception e){
            //this properties will be null if something goes wrong
        }
        file.setPermalink((String) rawFile.get("permalink"));
        file.setPermalinkPublic((String) rawFile.get("permalink_public"));
    }

    private static MessagePosted parseMessagePublishedWithFile(JSONObject obj, Channel channel, String ts, SlackSession slackSession)
    {
        SlackFile file = new SlackFile();
        if (obj.get("file")!=null){
            JSONObject rawFile = (JSONObject) obj.get("file");
	        parseSlackFileFromRaw(rawFile, file);
        }

        String text = (String) obj.get("text");
        String subtype = (String) obj.get("subtype");

        String comment = null;

        int idx = text.indexOf(COMMENT_PLACEHOLDER);

        if (idx != -1) {
            comment = text.substring(idx + COMMENT_PLACEHOLDER.length());
        }
        file.setComment(comment);

        String userId = (String) obj.get("user");

        User user = slackSession.findUserById(userId);

        return ImmutableMessagePosted.builder()
                .slackSession(slackSession)
                .channelId(channel.id())
                .ts(ts)
                .text(text)
                .userId(user.id())
                .jsonSource(obj)
//                .slackFile(Optional.of(file))
                .build();
    }

    private static Channel parseChannelDescription(JSONObject channelJSONObject) {
        String id = (String) channelJSONObject.get("id");
        String name = (String) channelJSONObject.get("name");
        String topic = (String)((Map)channelJSONObject.get("topic")).get("value");
        String purpose = (String) ((Map) channelJSONObject.get("purpose")).get("value");
        return ImmutableChannel.builder()
                .id(id)
                .name(name)
//                .topic(topic)
//                .purpose(purpose)
                .isIm(id.startsWith("D"))
                .isMember(id.startsWith("D"))
                .build();
    }


    private static ReactionAdded extractReactionAddedEvent(SlackSession slackSession, JSONObject obj) {
        JSONObject item = (JSONObject) obj.get("item");
        String emojiName = (String) obj.get("reaction");
        String messageId = (String) item.get("ts");
        String fileId = (String) item.get("file");
        String fileCommentId = (String) item.get("file_comment");
        String channelId = (String) item.get("channel");
        Channel channel = (channelId != null) ? slackSession.findChannelById(channelId) : null;
        User user = slackSession.findUserById((String) obj.get("user"));

        return ImmutableReactionAdded.builder()
                .slackSession(slackSession)
                .reaction(emojiName)
                .item(ImmutableItem.builder().channel(channelId).ts("toremove").type("toremove").build())
                .userId(user.id())
                .fileID(Optional.ofNullable(fileId))
                .fileCommentID(Optional.ofNullable(fileCommentId))
                .build();
    }

    private static UserChange extractUserChangeEvent(SlackSession slackSession, JSONObject obj) {
        JSONObject user = (JSONObject) obj.get("user");
        User slackUser = SlackJSONParsingUtils.buildSlackUser(user);
        return ImmutableUserChange.builder()
                .slackSession(slackSession)
                .user(slackUser)
                .build();
    }

    private static PresenceChanged extractPresenceChangeEvent(SlackSession slackSession, JSONObject obj) {
        String userId = (String) obj.get("user");
        String presence = (String) obj.get("presence");
        Presence value = Presence.UNKNOWN;
        if ("active".equals(presence)) {
            value = Presence.ACTIVE;
        } else if ("away".equals(presence)) {
            value = Presence.AWAY;
        }
        return ImmutablePresenceChanged.builder()
                .slackSession(slackSession)
                .user(userId)
                .presence(value)
                .build();
    }

    private static ReactionRemoved extractReactionRemovedEvent(SlackSession slackSession, JSONObject obj) {
        JSONObject item = (JSONObject) obj.get("item");
        String emojiName = (String) obj.get("reaction");
        String messageId = (String) item.get("ts");
        String fileId = (String) item.get("file");
        String fileCommentId = (String) item.get("file_comment");
        String channelId = (String) item.get("channel");
        Channel channel = (channelId != null) ? slackSession.findChannelById(channelId) : null;
        User user = slackSession.findUserById((String) obj.get("user"));

        return ImmutableReactionRemoved.builder()
                .slackSession(slackSession)
                .reaction(emojiName)
                .item(ImmutableItem.builder().channel(channelId).ts("toremove").type("toremove").build())
                .userId(user.id())
                .fileID(Optional.ofNullable(fileId))
                .fileCommentID(Optional.ofNullable(fileCommentId))
                .build();
    }

    private static PinRemoved extractPinRemovedEvent(SlackSession slackSession, JSONObject obj) {
        String senderId = (String) obj.get("user");
        User sender = slackSession.findUserById(senderId);

        String channelId = (String) obj.get("channel_id");
        Channel channel = slackSession.findChannelById(channelId);

        JSONObject item = (JSONObject) obj.get("item");
        String messageType = (String) item.get("type");
        SlackFile file = null;
        String message = null;
        if ("file".equals(messageType)) {
            file = new SlackFile();
            parseSlackFileFromRaw((JSONObject) item.get("file"), file);
        } else if ("message".equals(messageType)) {
            JSONObject messageObj = (JSONObject) item.get("message");
            message = (String) messageObj.get("text");
        }
        String timestamp = (String) obj.get("event_ts");

        return ImmutablePinRemoved.builder()
                .slackSession(slackSession)
                .channel(channel)
                .sender(sender)
                .timestamp(timestamp)
                .file(Optional.ofNullable(file))
                .message(message)
                .build();
    }

    private static PinAdded extractPinAddedEvent(SlackSession slackSession, JSONObject obj) {
        String senderId = (String) obj.get("user");
        User sender = slackSession.findUserById(senderId);

        String channelId = (String) obj.get("channel_id");
        Channel channel = slackSession.findChannelById(channelId);

        JSONObject item = (JSONObject) obj.get("item");
        String messageType = (String) item.get("type");
        SlackFile file = null;
        String message = null;
        if ("file".equals(messageType)) {
            file = new SlackFile();
            parseSlackFileFromRaw((JSONObject) item.get("file"), file);
        } else if ("message".equals(messageType)) {
            JSONObject messageObj = (JSONObject) item.get("message");
            message = (String) messageObj.get("text");
        }
        String timestamp = (String) obj.get("event_ts");

        return ImmutablePinAdded.builder()
                .slackSession(slackSession)
                .channel(channel)
                .sender(sender)
                .timestamp(timestamp)
                .file(Optional.ofNullable(file))
                .message(message)
                .build();
    }

    private static Map<String, Integer> extractReactionsFromMessageJSON(JSONObject obj) {
        Map<String, Integer> reacs = new HashMap<>();
        JSONArray rawReactions = (JSONArray) obj.get("reactions");
        if (rawReactions != null) {
            for (Object rawReaction : rawReactions) {
                JSONObject reaction = (JSONObject) rawReaction;
                String emojiCode = reaction.get("name").toString();
                Integer count = Integer.valueOf(reaction.get("count").toString());
                reacs.put(emojiCode, count);
            }
        }
        return reacs;
    }

    public static Map<String, String> extractEmojisFromMessageJSON(JSONObject object) {
        Map<String, String> emojis = new HashMap<>();

        for (Object o : object.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            emojis.put(entry.getKey().toString(), entry.getValue().toString());
        }

        return emojis;
    }

    private static ArrayList<SlackAttachment> extractAttachmentsFromMessageJSON(JSONObject object){
        if(object.get("attachments") == null) return new ArrayList<>();

        ArrayList<SlackAttachment> attachments = new ArrayList<>();

        for(Object o : (JSONArray) object.get("attachments")){
            JSONObject obj = (JSONObject) o;
            SlackAttachment slackAttachment = new SlackAttachment();

            slackAttachment.setFallback((String) obj.get("fallback"));
            slackAttachment.setColor((String) obj.get("color"));
            slackAttachment.setPretext((String) obj.get("pretext"));
            slackAttachment.setAuthorName((String) obj.get("author_name"));
            slackAttachment.setAuthorLink((String) obj.get("author_link"));
            slackAttachment.setAuthorIcon((String) obj.get("author_icon"));
            slackAttachment.setTitle((String) obj.get("title"));
            slackAttachment.setTitleLink((String) obj.get("title_link"));
            slackAttachment.setText((String) obj.get("text"));
            slackAttachment.setThumbUrl((String) obj.get("thumb_url"));
            slackAttachment.setImageUrl((String) obj.get("image_url"));
            slackAttachment.setFooter((String) obj.get("footer"));
            slackAttachment.setFooterIcon((String) obj.get("footer_icon"));

            if(obj.get("fields") != null) {
                for (Object field : (JSONArray) obj.get("fields")) {
                    JSONObject f = (JSONObject) field;
                    slackAttachment.addField((String) f.get("title"), (String) f.get("value"),
                        (Boolean) f.get("short"));
                }
            }

            attachments.add(slackAttachment);
        }

        return attachments;
    }
}


