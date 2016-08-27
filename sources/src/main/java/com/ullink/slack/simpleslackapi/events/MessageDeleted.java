package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import org.immutables.value.Value;

@Value.Immutable
public interface MessageDeleted extends MessageEvent {
    SlackChannel channel();
    String deletedTimestamp();
}
