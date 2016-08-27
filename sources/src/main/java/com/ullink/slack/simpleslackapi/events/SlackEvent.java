package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackSession;

public interface SlackEvent {
    SlackSession slackSession();
}
