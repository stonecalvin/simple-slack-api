package com.ullink.slack.simpleslackapi.json;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class Message {
    public abstract String user();
    public abstract String ts();
    public abstract Optional<String> text();
    public abstract Optional<Message> edited();
}
