package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackPersona;
import org.immutables.value.Value;

@Value.Immutable
public interface Disconnected extends SlackEvent {
    SlackPersona slackPersona();
}
