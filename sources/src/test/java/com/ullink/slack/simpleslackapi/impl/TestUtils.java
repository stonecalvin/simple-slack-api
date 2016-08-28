package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.json.*;

public class TestUtils {
    public static Channel generateChannel(String identifier) {
        return ImmutableChannel.builder()
                .id("channelid" + identifier)
                .name("testchannel" + identifier)
                .topic("topicchannel" + identifier)
                .purpose("topicchannel" + identifier)
                .build();
    }

    public static User generateUser(String identifier) {
        Profile profile = ImmutableProfile.builder()
                .email("userid" + identifier + "@my.mail")
                .phone("testPhone")
                .skype("testSkype")
                .title("testTitle")
                .build();

        return ImmutableUser.builder()
                .id("userid" + identifier)
                .name("username" + identifier)
                .realName("realname" + identifier)
                .profile(profile)
                .tz("tz")
                .tzLabel("tzLabel")
                .tzOffset(new Integer(0))
                .presence(Presence.ACTIVE)
                .build();
    }

    public static User generateBot(String identifier) {
        return ImmutableUser.copyOf(generateUser(identifier))
                .withId("botid" + identifier)
                .withName("botname" + identifier)
                .withBot(true);
    }
}
