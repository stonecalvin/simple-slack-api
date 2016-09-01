package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.json.User;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class ChannelUnarchived extends ChannelEvent {
    public static final String type = "channel_unarchive";
    public abstract User user();
}
