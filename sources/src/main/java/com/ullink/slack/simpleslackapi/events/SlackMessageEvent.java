package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.immutables.value.Value;

public abstract class SlackMessageEvent extends SlackEvent {
    public static final String type = "message";

    @SerializedName("ts") public abstract String getTimestamp();
    @SerializedName("channel") public abstract String getChannelId();

    @Value.Lazy
    public SlackChannel getChannel() {
        return this.getSlackSession().findChannelById(this.getChannelId());
    }

}
