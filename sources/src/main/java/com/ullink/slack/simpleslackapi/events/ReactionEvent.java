package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.json.Channel;
import com.ullink.slack.simpleslackapi.json.Item;
import com.ullink.slack.simpleslackapi.json.User;
import org.immutables.value.Value;

import java.util.Optional;

public abstract class ReactionEvent extends SlackEvent {
    public abstract Item item();
    public abstract String reaction();

    public abstract String event_ts();
    public abstract String ts();

    @SerializedName("user") public abstract java.lang.String userId();

    @Value.Lazy
    public Channel channel() {
        return this.slackSession().findChannelById(this.item().channel());
    }

    @Value.Lazy
    public User user() {
        return this.slackSession().findUserById(this.userId());
    }

    public abstract Optional<String> fileID();
    public abstract Optional<String> fileCommentID();
}
