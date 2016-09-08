package com.ullink.slack.simpleslackapi;

import com.ullink.slack.simpleslackapi.json.MyAttachment;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
public abstract class MyPreparedMessage {
    public abstract String message();
    public abstract Optional<Boolean> unfurl();
    public abstract Optional<Boolean> linkNames();
    public abstract Optional<List<MyAttachment>> attachments();

}
