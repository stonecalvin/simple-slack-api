package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.json.Channel;
import org.immutables.value.Value;

@Value.Immutable
public interface MessageDeleted extends MessageEvent {
    Channel channel();
    String deletedTimestamp();
}
