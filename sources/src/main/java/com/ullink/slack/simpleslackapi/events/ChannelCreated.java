package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.value.Value;

@Value.Immutable
public interface ChannelCreated extends ChannelEvent {
    SlackUser user();
}
