package com.ullink.slack.simpleslackapi.events;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class ReactionAdded extends ReactionEvent {
    public static final String type = "reaction_added";

}
