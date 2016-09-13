package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackFile;
import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class PinRemoved extends SlackEvent {
    public static final String type = "pin_removed";

    public abstract SlackUser getSender();
    public abstract SlackChannel getChannel();
    public abstract String getTimestamp();
    public abstract String getMessage();

    public abstract Optional<SlackFile> getFile();

}
