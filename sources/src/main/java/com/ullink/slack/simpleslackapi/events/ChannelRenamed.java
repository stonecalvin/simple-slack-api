package com.ullink.slack.simpleslackapi.events;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class ChannelRenamed extends ChannelEvent {
    public static final String type = "channel_rename";
    public abstract String newName();
}
