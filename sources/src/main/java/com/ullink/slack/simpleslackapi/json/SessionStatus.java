package com.ullink.slack.simpleslackapi.json;

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
    List<Channel> channels();
    List<Channel> groups();
    List<Channel> ims();

    @Value.Lazy
    default Map<String, Channel> channelMap() {
        return Stream.concat(this.ims().stream(),
               Stream.concat(this.channels().stream(),
                             this.groups().stream()))
                     .collect(Collectors.toMap(Channel::id, Function.identity()));
    }

    List<User> users();

    // Original version called these "integrations" but pulled from bots field of json
    List<Integration> bots();

    User self();
    Team team();

    String url();
}
