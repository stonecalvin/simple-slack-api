package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackChannelArchived extends SlackChannelEvent {
    public static final String type = "channel_archive";
    public abstract SlackUser getUser();
}
