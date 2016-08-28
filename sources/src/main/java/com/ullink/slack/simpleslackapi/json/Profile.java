package com.ullink.slack.simpleslackapi.json;


import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public interface Profile {
    Optional<String> email();
    Optional<String> skype();
    Optional<String> title();
    Optional<String> phone();
}
