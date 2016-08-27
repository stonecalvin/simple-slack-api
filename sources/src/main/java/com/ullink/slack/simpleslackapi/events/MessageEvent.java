package com.ullink.slack.simpleslackapi.events;

public interface MessageEvent extends SlackEvent {
    String timestamp();
}
