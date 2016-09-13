package com.ullink.slack.simpleslackapi.events;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackGroupJoined extends SlackChannelEvent {
    public static final String type = "group_joined";
}
