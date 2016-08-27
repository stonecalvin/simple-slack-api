package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.value.Value;

@Value.Immutable
public interface UserChange extends SlackEvent {
    SlackUser user();
}
