package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.SlackMessage;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackMessageUpdated extends SlackMessageEvent {
    public static final String type = "message_changed";

    @SerializedName("event_ts") public abstract String getMessageTimestamp();

    public abstract SlackMessage getMessage();
    public abstract Optional<List<SlackAttachment>> getAttachments();
}
