package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackChannelDeleted extends SlackChannelEvent {
    public static final String type = "channel_deleted";

    @SerializedName("channel") public abstract String getChannelId();

    @Value.Lazy
    @Override
    public SlackChannel getChannel() {
        return this.getSlackSession().findChannelById(this.getChannelId());
    }
}
