package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.json.Channel;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class ChannelLeft extends ChannelEvent {
    public static final String type = "channel_left";

    // ChannelLeft event has a string for channel instead of an actual channel object
    @SerializedName("channel") public abstract String channelId();

    @Value.Lazy
    public Channel channel() {
        return this.slackSession().findChannelById(this.channelId());
    }

}
