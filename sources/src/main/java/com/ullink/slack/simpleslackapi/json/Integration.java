package com.ullink.slack.simpleslackapi.json;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface Integration {
    String id();
    String name();

    @Value.Default
    default boolean deleted() {
        return false;
    }
}
