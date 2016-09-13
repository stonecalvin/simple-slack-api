package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.SlackFile;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class PinAdded extends SlackEvent {
    public static final String type = "pin_added";

    public abstract SlackUser getSender();
    public abstract SlackChannel getChannel();
    public abstract String getTimestamp();
    public abstract String getMessage();

    public abstract Optional<SlackFile> getFile();

}
