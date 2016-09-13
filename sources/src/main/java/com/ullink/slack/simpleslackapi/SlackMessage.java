package com.ullink.slack.simpleslackapi;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackMessage {
    public abstract String getUser();
    public abstract Optional<String> getText();
    public abstract Optional<SlackMessage> getEdited();

    @SerializedName("ts") public abstract String getTimestamp();
}
