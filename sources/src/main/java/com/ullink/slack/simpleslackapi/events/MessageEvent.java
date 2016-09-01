package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.json.Channel;
import org.immutables.value.Value;

public abstract class MessageEvent extends SlackEvent {
    public static final String type = "message";
    public abstract String ts();

    @SerializedName("channel") public abstract String channelId();

    @Value.Lazy
    public Channel channel() {
        return this.slackSession().findChannelById(this.channelId());
    }

}
