package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackFile;
import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.value.Value;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
public interface MessagePosted extends MessageEvent {
    enum MessageSubType {
        BOT_MESSAGE("bot_message"),
        CHANNEL_ARCHIVE("channel_archive"),
        CHANNEL_JOIN("channel_join"),
        CHANNEL_LEAVE("channel_leave"),
        CHANNEL_LEFT("channel_left"),
        CHANNEL_NAME("channel_name"),
        CHANNEL_PURPOSE("channel_purpose"),
        CHANNEL_TOPIC("channel_topic"),
        CHANNEL_UNARCHIVE("channel_unarchive"),
        FILE_COMMENT("file_comment"),
        FILE_MENTION("file_mention"),
        FILE_SHARE("file_share"),
        GROUP_JOIN("group_join"),
        GROUP_LEAVE("group_leave"),
        GROUP_NAME("group_name"),
        GROUP_PURPOSE("group_purpose"),
        GROUP_TOPIC("group_topic"),
        GROUP_UNARCHIVE("group_unarchive"),
        ME_MESSAGE("me_message"),
        MESSAGE_CHANGED("message_changed"),
        MESSAGE_DELETED("message_deleted"),
        PINNED_ITEM("pinned_item"),
        UNPINNED_ITEM("unpinned_item"),
        UNKNOWN("");

        String code;

        MessageSubType(String code) {
            this.code = code;
        }

        static public final MessagePosted.MessageSubType fromCode(String code) {
            return Arrays.stream(MessageSubType.values())
                    .filter(subType -> subType.code.equals(code))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }

    String messageContent();
    SlackUser sender();
    SlackChannel channel();
    JSONObject jsonSource();
    MessagePosted.MessageSubType messageSubType();

    Optional<Map<String, Integer>> reactions();
    Optional<ArrayList<SlackAttachment>> attachments();
    Optional<SlackFile> slackFile();
}
