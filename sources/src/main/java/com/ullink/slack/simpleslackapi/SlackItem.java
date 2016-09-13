package com.ullink.slack.simpleslackapi;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackItem {
    public abstract String getType();
    public abstract Optional<String> getChannel();
    @SerializedName("ts") public abstract Optional<String> getTimestamp();
}
