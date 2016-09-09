package com.ullink.slack.simpleslackapi.replies;

import org.immutables.value.Value;
import org.json.simple.JSONObject;

@Value.Immutable
public interface GenericSlackReply extends SlackReply {
    JSONObject plainAnswer();
}
