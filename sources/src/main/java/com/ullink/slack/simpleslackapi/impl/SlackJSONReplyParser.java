package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.replies.ParsedSlackReply;
import org.json.simple.JSONObject;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.replies.SlackChannelReply;

import java.util.HashMap;
import java.util.Map;

class SlackJSONReplyParser {
    static ParsedSlackReply decode(JSONObject obj, SlackSession session)
    {
        Boolean ok = (Boolean) obj.get("ok");
        String error = (String) obj.get("error");

        String presence = (String)obj.get("presence");
        if (presence != null) {
            return new SlackUserPresenceReplyImpl(ok, error,"active".equals(presence));
        }

        if (isMpim(obj) || isIm(obj) || isChannel(obj) || isGroup(obj)) {
            return buildSlackChannelReply(ok,error,obj,session);
        }

        if(isMessageReply(obj)) {
            Long replyTo = (Long) obj.get("reply_to");
            String timestamp = (String) obj.get("ts");
            return new SlackMessageReplyImpl(ok, error, obj, replyTo != null ? replyTo : -1, timestamp);
        }

        if (isEmojiReply(obj)) {
            String timestamp = (String) obj.get("cache_ts");
            return new SlackEmojiReplyImpl(ok, error, extractEmojisFromMessageJSON((JSONObject) obj.get("emoji")), timestamp);
        }

        if (ok == null) {
            //smelly reply
            ok = Boolean.FALSE;
        }
        return new SlackReplyImpl(ok,error);
    }

    private static Map<String, String> extractEmojisFromMessageJSON(JSONObject object) {
        Map<String, String> emojis = new HashMap<>();

        for (Object o : object.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            emojis.put(entry.getKey().toString(), entry.getValue().toString());
        }

        return emojis;
    }

    private static SlackChannelReply buildSlackChannelReply(Boolean ok, String error, JSONObject obj, SlackSession session) {
        String id = (String)obj.get("id");
        if (id != null) {
            return new SlackChannelReplyImpl(ok,error,obj, session.findChannelById(id));
        }

        JSONObject channelObj = (JSONObject) obj.get("channel");
        if (channelObj == null) {
            channelObj = (JSONObject) obj.get("group");
        }

        id = (String)channelObj.get("id");
        return new SlackChannelReplyImpl(ok,error,obj, session.findChannelById(id));
    }

    private static boolean isMessageReply(JSONObject obj)
    {
        return obj.get("ts") != null;
    }

    private static boolean isMpim(JSONObject obj) {
        Boolean isMpim = (Boolean)obj.get("is_mpim");
        return isMpim != null && isMpim.equals(Boolean.TRUE);
    }

    private static boolean isIm(JSONObject obj) {
        Boolean isIm = (Boolean)obj.get("is_im");
        return isIm != null && isIm.equals(Boolean.TRUE);
    }

    private static boolean isChannel(JSONObject obj) {
        Object channel = obj.get("channel");
        return channel != null && channel instanceof JSONObject;
    }

    private static boolean isGroup(JSONObject obj) {
        Boolean isGroup = (Boolean)obj.get("is_group");
        if (isGroup != null) {
            return isGroup;
        }

        Object group = obj.get("group");
        return group != null && group instanceof JSONObject;
    }

    private static boolean isEmojiReply(JSONObject obj) {
        Object emoji = obj.get("emoji");
        return emoji != null && emoji instanceof JSONObject;
    }

}
