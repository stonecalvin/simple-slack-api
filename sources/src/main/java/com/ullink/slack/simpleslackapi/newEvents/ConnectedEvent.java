package com.ullink.slack.simpleslackapi.newEvents;

import com.ullink.slack.simpleslackapi.SlackPersona;
import com.ullink.slack.simpleslackapi.SlackSession;
import org.immutables.value.Value;

@Value.Immutable
public interface ConnectedEvent {
    SlackPersona slackPersona();
    SlackSession slackSession();
}
