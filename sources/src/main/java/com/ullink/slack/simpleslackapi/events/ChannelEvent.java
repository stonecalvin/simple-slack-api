package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;

public interface ChannelEvent extends SlackEvent {
    SlackChannel channel();
}
