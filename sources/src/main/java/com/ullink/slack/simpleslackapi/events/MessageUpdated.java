package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Optional;

@Value.Immutable
public interface MessageUpdated extends MessageEvent {
    SlackChannel channel();
    String messageTimestamp();
    String message();

    Optional<ArrayList<SlackAttachment>> attachments();
}
