package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackChannelLeft extends SlackChannelEvent {
    public static final String type = "channel_left";

    // SlackChannelLeft event has a string for channel instead of an actual channel object
    @SerializedName("channel") public abstract String getChannelId();

    @Value.Lazy
    @Override
    public SlackChannel getChannel() {
        return this.getSlackSession().findChannelById(this.getChannelId());
    }

}
