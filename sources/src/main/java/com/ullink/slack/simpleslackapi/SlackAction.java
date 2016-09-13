package com.ullink.slack.simpleslackapi;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackAction {
    public static final String TYPE_BUTTON = "button";

    public abstract String getName();
    public abstract String getText();
    public abstract String getType();
    public abstract String getValue();

}
