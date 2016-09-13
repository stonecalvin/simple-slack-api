package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.*;

public class TestUtils {
    public static SlackChannel generateChannel(String identifier) {
        return ImmutableSlackChannel.builder()
                .id("channelid" + identifier)
                .name("testchannel" + identifier)
                .topic(ImmutableSlackTopic.builder()
                        .value("topicchannel" + identifier)
                        .lastSet(0)
                        .build())
                .purpose(ImmutableSlackTopic.builder()
                        .value("purpose" + identifier)
                        .lastSet(0)
                        .build())
                .build();
    }

    public static SlackUser generateUser(String identifier) {
        SlackProfile slackProfile = ImmutableSlackProfile.builder()
                .email("userid" + identifier + "@my.mail")
                .phone("testPhone")
                .skype("testSkype")
                .title("testTitle")
                .build();

        return ImmutableSlackUser.builder()
                .id("userid" + identifier)
                .name("username" + identifier)
                .realName("realname" + identifier)
                .profile(slackProfile)
                .timezone("tz")
                .timezoneLabel("tzLabel")
                .timezoneOffset(new Integer(0))
                .presence(PresenceEnum.ACTIVE)
                .build();
    }

    public static SlackUser generateBot(String identifier) {
        return ImmutableSlackUser.copyOf(generateUser(identifier))
                .withId("botid" + identifier)
                .withName("botname" + identifier)
                .withIsBot(true);
    }
}
