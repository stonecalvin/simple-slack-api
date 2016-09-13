package com.ullink.slack.simpleslackapi;

import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
public abstract class SlackPreparedMessage {
    public abstract String getMessage();
    public abstract Optional<Boolean> isUnfurl();
    public abstract Optional<Boolean> isLinkNames();
    public abstract Optional<List<SlackAttachment>> getAttachments();

}
