package com.ullink.slack.simpleslackapi.events;

import org.immutables.value.Value;

@Value.Immutable
public interface ChannelRenamed extends ChannelEvent {
    String newName();
}
