package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.json.Channel;

public interface ChannelEvent extends SlackEvent {
    Channel channel();
}
