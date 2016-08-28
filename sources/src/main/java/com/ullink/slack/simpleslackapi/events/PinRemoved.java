package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackFile;
import com.ullink.slack.simpleslackapi.json.Channel;
import com.ullink.slack.simpleslackapi.json.User;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface PinRemoved extends SlackEvent {
    User sender();
    Channel channel();
    String timestamp();
    String message();

    Optional<SlackFile> file();

}
