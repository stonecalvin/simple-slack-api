package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackSession;

import javax.annotation.Nullable;

public abstract class SlackEvent {
    @Nullable public abstract SlackSession slackSession();
}
