package com.ullink.slack.simpleslackapi;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface SlackIntegration {
    String getId();
    String getName();

    @Value.Default
    default boolean isDeleted() {
        return false;
    }
}
