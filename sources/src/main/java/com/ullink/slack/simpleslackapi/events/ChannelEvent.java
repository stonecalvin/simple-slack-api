package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.json.Channel;

public abstract class ChannelEvent extends SlackEvent {
    public abstract Channel channel();
}
