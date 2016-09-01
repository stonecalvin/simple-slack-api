package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackFile;
import com.ullink.slack.simpleslackapi.json.Channel;
import com.ullink.slack.simpleslackapi.json.User;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class PinAdded extends SlackEvent {
    public static final String type = "pin_added";

    public abstract User sender();
    public abstract Channel channel();
    public abstract String timestamp();
    public abstract String message();

    public abstract Optional<SlackFile> file();

}
