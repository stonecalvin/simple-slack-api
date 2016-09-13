package com.ullink.slack.simpleslackapi;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackField {
    public abstract String getTitle();
    public abstract String getValue();
    public abstract boolean isShort();
}
