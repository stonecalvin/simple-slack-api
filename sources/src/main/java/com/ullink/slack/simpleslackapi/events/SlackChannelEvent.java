package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;

public abstract class SlackChannelEvent extends SlackEvent {
    public abstract SlackChannel getChannel();
}
