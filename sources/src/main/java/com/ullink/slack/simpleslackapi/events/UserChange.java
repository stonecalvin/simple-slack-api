package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.json.User;
import org.immutables.value.Value;

@Value.Immutable
public interface UserChange extends SlackEvent {
    User user();
}
