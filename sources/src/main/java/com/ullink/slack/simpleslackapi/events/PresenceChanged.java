package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackPersona;
import org.immutables.value.Value;

@Value.Immutable
public interface PresenceChanged extends SlackEvent {
    String userId();
    SlackPersona.SlackPresence presence();
}
