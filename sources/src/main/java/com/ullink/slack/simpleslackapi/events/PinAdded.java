package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackFile;
import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface PinAdded extends SlackEvent {
    SlackUser sender();
    SlackChannel channel();
    String timestamp();
    String message();

    Optional<SlackFile> file();

}
