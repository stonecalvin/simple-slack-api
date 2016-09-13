package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackChannelUnarchived extends SlackChannelEvent {
    public static final String type = "channel_unarchive";
    public abstract SlackUser getUser();
}
