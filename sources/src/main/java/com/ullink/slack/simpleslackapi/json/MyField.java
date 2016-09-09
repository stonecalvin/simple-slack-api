package com.ullink.slack.simpleslackapi.json;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class MyField {
    public abstract String title();
    public abstract String value();
    public abstract boolean isShort();
}
