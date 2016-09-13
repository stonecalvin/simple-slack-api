package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackMessageDeleted extends SlackMessageEvent {
    public static final String type = "message_deleted";

    @SerializedName("deleted_ts") public abstract String getDeletedTimestamp();

}
