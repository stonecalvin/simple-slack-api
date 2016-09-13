package com.ullink.slack.simpleslackapi;


import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public interface SlackProfile {
    Optional<String> getEmail();
    Optional<String> getSkype();
    Optional<String> getTitle();
    Optional<String> getPhone();
}
