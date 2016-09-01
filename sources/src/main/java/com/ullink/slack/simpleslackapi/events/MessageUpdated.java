package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.json.Message;
import com.ullink.slack.simpleslackapi.json.MyAttachment;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class MessageUpdated extends MessageEvent {
    public static final String type = "message_changed";

    @SerializedName("event_ts") public abstract String messageTimestamp();

    public abstract Message message();
    public abstract Optional<List<MyAttachment>> attachments();
}
