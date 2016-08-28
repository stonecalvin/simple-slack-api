package com.ullink.slack.simpleslackapi.json;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

// Todo: I dont like this class...think of a better way to model it later
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
