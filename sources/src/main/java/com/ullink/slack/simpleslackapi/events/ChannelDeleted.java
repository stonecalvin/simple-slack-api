package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.json.Channel;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class ChannelDeleted extends ChannelEvent {
    public static final String type = "channel_deleted";

    @SerializedName("channel") public abstract java.lang.String channelId();

    @Value.Lazy
    public Channel channel() {
        return this.slackSession().findChannelById(this.channelId());
    }
}
