package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.json.Presence;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class PresenceChanged extends SlackEvent {
    public static final String type = "presence_change";

    public abstract String user();
    public abstract Presence presence();
}
