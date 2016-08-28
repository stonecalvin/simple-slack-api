package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.json.*;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Optional;

class SlackJSONParsingUtils {

    private SlackJSONParsingUtils() {
        // Helper class
    }

    static final User buildSlackUser(JSONObject jsonUser)
    {
        String id = (String) jsonUser.get("id"); //userSkype, userTitle, userPhone
        String name = (String) jsonUser.get("name");
        String realName = (String) jsonUser.get("real_name");
        String tz = (String) jsonUser.get("tz");
        String tzLabel = (String) jsonUser.get("tz_label");
        Long tzOffset = ((Long) jsonUser.get("tz_offset"));
        Boolean deleted = ifNullFalse(jsonUser, "deleted");
        Boolean admin = ifNullFalse(jsonUser, "is_admin");
        Boolean owner = ifNullFalse(jsonUser, "is_owner");
        Boolean primaryOwner = ifNullFalse(jsonUser, "is_primary_owner");
        Boolean restricted = ifNullFalse(jsonUser, "is_restricted");
        Boolean ultraRestricted = ifNullFalse(jsonUser, "is_ultra_restricted");
        Boolean bot = ifNullFalse(jsonUser, "is_bot");
        JSONObject profileJSON = (JSONObject) jsonUser.get("profile");
        String email = "";
        String skype = "";
        String title = "";
        String phone = "";
        if (profileJSON != null)
        {
            email = (String) profileJSON.get("email");
            skype = (String) profileJSON.get("skype");
            title = (String) profileJSON.get("title");
            phone = (String) profileJSON.get("phone");
        }

        String presence = (String) jsonUser.get("presence");
        Presence slackPresence = Presence.UNKNOWN;
        if ("active".equals(presence))
        {
            slackPresence = Presence.ACTIVE;
        }
        if ("away".equals(presence))
        {
            slackPresence = Presence.AWAY;
        }

        // TODO: Gson this...
        Profile profile = ImmutableProfile.builder()
                .email(Optional.ofNullable(email))
                .phone(Optional.ofNullable(phone))
                .skype(Optional.ofNullable(skype))
                .title(Optional.ofNullable(title))
                .build();
        return ImmutableUser.builder()
                .id(id)
                .name(name)
                .realName(Optional.ofNullable(realName))
                .profile(profile)
                .deleted(deleted)
                .admin(admin)
                .owner(owner)
                .primaryOwner(primaryOwner)
                .restricted(restricted)
                .ultraRestricted(ultraRestricted)
                .bot(bot)
                .tz(Optional.ofNullable(tz))
                .tzLabel(Optional.ofNullable(tzLabel))
                .tzOffset(Optional.ofNullable(tzOffset == null ? null : Long.valueOf(tzOffset.intValue())))
                .presence(slackPresence)
                .build();
    }

    private static Boolean ifNullFalse(JSONObject jsonUser, String field) {
        Boolean deleted = (Boolean) jsonUser.get(field);
        if (deleted == null) {
            deleted = false;
        }
        return deleted;
    }

    static final Channel buildSlackChannel(JSONObject jsonChannel, Map<String, User> knownUsersById) {
        String id = (String) jsonChannel.get("id");
        String name = (String) jsonChannel.get("name");

        String topic = null;
        if (jsonChannel.containsKey("topic")) {
            JSONObject jsonTopic = (JSONObject)jsonChannel.get("topic");
            topic = (String)jsonTopic.get("value");
        }

        String purpose = null;
        if (jsonChannel.containsKey("purpose")) {
            JSONObject jsonPurpose = (JSONObject)jsonChannel.get("purpose");
            purpose = (String) jsonPurpose.get("value");
        }

        boolean isMember = false;
        if (jsonChannel.containsKey("is_member")) {
            isMember = (boolean)jsonChannel.get("is_member");
        }

        return ImmutableChannel.builder()
                .id(id)
                .name(name)
                .topic(Optional.ofNullable(topic))
                .purpose(Optional.ofNullable(purpose))
                .isMember(isMember)
                .build();
    }

    static final Channel buildSlackImChannel(JSONObject jsonChannel, Map<String, User> knownUsersById) {
        String id = (String) jsonChannel.get("id");
        String memberId = (String) jsonChannel.get("user");
        return ImmutableChannel.builder()
                .id(id)
                .user(memberId)
                .build();
    }

    static final Team buildSlackTeam(JSONObject jsonTeam) {
        String id = (String) jsonTeam.get("id");
        String name = (String) jsonTeam.get("name");
        String domain = (String) jsonTeam.get("domain");
        return ImmutableTeam.builder()
                .id(id)
                .name(name)
                .domain(domain)
                .build();
    }

    static final Integration buildSlackIntegration(JSONObject jsonIntegration) {
        String id = (String) jsonIntegration.get("id");
        String name = (String) jsonIntegration.get("name");
        boolean deleted = ifNullFalse(jsonIntegration, "deleted");
        return ImmutableIntegration.builder()
                .id(id)
                .name(name)
                .deleted(deleted)
                .build();
    }
}
