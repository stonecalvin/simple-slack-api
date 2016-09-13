package com.ullink.slack.simpleslackapi.events;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackFile;
import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.json.simple.JSONObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public abstract class SlackMessagePosted extends SlackMessageEvent {
    public abstract String getText();

    @Nullable public abstract JSONObject getJsonSource();

    @SerializedName("user") public abstract String getUserId();
    @SerializedName("team") public abstract String getTeamId();

    @Value.Lazy
    public SlackUser getSender() {
        return this.getSlackSession().findUserById(this.getUserId());
    }

    public abstract Optional<Map<String, Integer>> getReactions();
    public abstract Optional<List<SlackAttachment>> getAttachments();
    public abstract Optional<SlackFile> getSlackFile();
}
