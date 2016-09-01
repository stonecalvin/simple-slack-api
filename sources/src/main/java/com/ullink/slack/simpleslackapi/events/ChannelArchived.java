package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.json.User;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class ChannelArchived extends ChannelEvent {
    public static final String type = "channel_archive";
    public abstract User user();
}
