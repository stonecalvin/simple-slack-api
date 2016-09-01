package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.json.User;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class UserChange extends SlackEvent {
    public static final String type = "user_change";

    public abstract User user();
}
