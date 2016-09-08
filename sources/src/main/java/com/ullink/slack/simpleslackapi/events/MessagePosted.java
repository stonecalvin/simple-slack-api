package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.json.MyAttachment;
import com.ullink.slack.simpleslackapi.json.MyFile;
import com.ullink.slack.simpleslackapi.json.User;
import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.json.simple.JSONObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class MessagePosted extends MessageEvent {
    public abstract String text();

    @Nullable public abstract JSONObject jsonSource();

    @SerializedName("user") public abstract String userId();
    @SerializedName("team") public abstract String teamId();

    @Value.Lazy
    public User sender() {
        return this.slackSession().findUserById(this.userId());
    }

    public abstract Optional<Map<String, Integer>> reactions();
    public abstract Optional<List<MyAttachment>> attachments();
    public abstract Optional<MyFile> slackFile();
}
