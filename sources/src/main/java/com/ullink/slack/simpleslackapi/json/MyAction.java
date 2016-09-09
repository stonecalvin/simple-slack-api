package com.ullink.slack.simpleslackapi.json;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class MyAction {
    public static final String TYPE_BUTTON = "button";

    public abstract String name();
    public abstract String text();
    public abstract String type();
    public abstract String value();

}
