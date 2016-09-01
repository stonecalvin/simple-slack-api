package com.ullink.slack.simpleslackapi.json;


import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public interface Topic {
    String value();
    Optional<String> creator();
    @SerializedName("last_set") int lastSet();
}
