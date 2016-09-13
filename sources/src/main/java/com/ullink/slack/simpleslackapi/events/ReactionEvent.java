package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.SlackItem;
import org.immutables.value.Value;

import java.util.Optional;

public abstract class ReactionEvent extends SlackEvent {
    public abstract SlackItem getItem();
    public abstract String getReaction();

    @SerializedName("event_ts") public abstract String getEventTimestamp();

    @SerializedName("user") public abstract String getUserId();

    @Value.Lazy
    public Optional<SlackChannel> getChannel() {
        if (this.getSlackSession() != null && this.getItem().getChannel().isPresent()) {
            return Optional.of(this.getSlackSession().findChannelById(this.getItem().getChannel().get()));
        }

        return Optional.empty();
    }

    @Value.Lazy
    public SlackUser getUser() {
        return this.getSlackSession().findUserById(this.getUserId());
    }

    public abstract Optional<String> getFileID();
    public abstract Optional<String> getFileCommentID();
}
