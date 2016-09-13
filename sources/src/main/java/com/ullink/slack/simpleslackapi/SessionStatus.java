package com.ullink.slack.simpleslackapi;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Gson.TypeAdapters
@Value.Immutable
public interface SessionStatus {
    @SerializedName("channels") List<SlackChannel> getSlackChannels();
    @SerializedName("groups") List<SlackChannel> getSlackGroups();
    @SerializedName("ims") List<SlackChannel> getSlackIms();

    @Value.Lazy
    default Map<String, SlackChannel> getChannelMap() {
        return Stream.concat(this.getSlackIms().stream(),
               Stream.concat(this.getSlackChannels().stream(),
                             this.getSlackGroups().stream()))
                     .collect(Collectors.toMap(SlackChannel::getId, Function.identity()));
    }

    @SerializedName("users") List<SlackUser> getSlackUsers();

    @SerializedName("bots") List<SlackIntegration> getIntegrations();

    SlackUser getSelf();
    SlackTeam getTeam();

    String getUrl();
}
