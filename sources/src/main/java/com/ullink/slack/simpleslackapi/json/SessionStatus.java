package com.ullink.slack.simpleslackapi.json;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface SessionStatus {
    List<Channel> channels();
    List<Channel> groups();
    List<Channel> ims();

    List<User> users();

    // Original version called these "integrations" but pulled from bots field of json
    List<Integration> bots();

    User self();
    Team team();

    String url();
}
