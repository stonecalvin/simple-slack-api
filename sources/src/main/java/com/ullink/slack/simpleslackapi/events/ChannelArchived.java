package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.value.Value;

@Value.Immutable
public interface ChannelArchived extends ChannelEvent {
    SlackUser user();
}
