package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.PresenceEnum;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class PresenceChange extends SlackEvent {
    public static final String type = "presence_change";

    public abstract String getUser();
    public abstract PresenceEnum getPresence();
}
