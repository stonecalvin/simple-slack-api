package com.ullink.slack.simpleslackapi;


import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public interface SlackTopic {
    String getValue();
    Optional<String> getCreator();
    @SerializedName("last_set") int getLastSet();
}
