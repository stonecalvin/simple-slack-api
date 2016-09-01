package com.ullink.slack.simpleslackapi.json;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class Item {
    public abstract String type();
    public abstract String channel();
    public abstract String ts();
}
