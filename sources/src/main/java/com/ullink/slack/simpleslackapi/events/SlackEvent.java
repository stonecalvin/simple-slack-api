package com.ullink.slack.simpleslackapi.events;

import com.sun.istack.internal.Nullable;
import com.ullink.slack.simpleslackapi.SlackSession;

public abstract class SlackEvent {
    @Nullable public abstract SlackSession slackSession();
}
