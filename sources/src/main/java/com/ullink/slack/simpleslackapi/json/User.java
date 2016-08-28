package com.ullink.slack.simpleslackapi.json;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public interface User {
    String id();
    String name();
    @SerializedName("real_name") Optional<String> realName();
    Optional<String> tz();
    @SerializedName("tz_label") Optional<String> tzLabel();
    @SerializedName("tz_offset") Optional<Long> tzOffset();

    @Value.Default
    default boolean deleted() {
        return false;
    }

    @Value.Default
    @SerializedName("is_admin") default boolean admin() {
        return false;
    }

    @Value.Default
    @SerializedName("is_owner") default boolean owner() {
        return false;
    }

    @Value.Default
    @SerializedName("is_primary_owner")
    default boolean primaryOwner() {
        return false;
    }

    @Value.Default
    @SerializedName("is_restricted") default boolean restricted() {
        return false;
    }

    @Value.Default
    @SerializedName("is_ultra_restricted") default boolean ultraRestricted() {
        return false;
    }

    @Value.Default
    @SerializedName("is_bot") default boolean bot() {
        return false;
    }

    @Value.Default
    default Presence presence() {
        return Presence.UNKNOWN;
    }

    Optional<Profile> profile();
}
