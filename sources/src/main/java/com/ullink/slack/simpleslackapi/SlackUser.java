package com.ullink.slack.simpleslackapi;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public interface SlackUser {
    String getId();
    String getName();
    @SerializedName("real_name") Optional<String> getRealName();
    @SerializedName("tz") Optional<String> getTimezone();
    @SerializedName("tz_label") Optional<String> getTimezoneLabel();
    @SerializedName("tz_offset") Optional<Long> getTimezoneOffset();

    @Value.Default
    default boolean isDeleted() {
        return false;
    }

    @Value.Default
    @SerializedName("is_admin") default boolean isAdmin() {
        return false;
    }

    @Value.Default
    @SerializedName("is_owner") default boolean isOwner() {
        return false;
    }

    @Value.Default
    @SerializedName("is_primary_owner")
    default boolean isPrimaryOwner() {
        return false;
    }

    @Value.Default
    @SerializedName("is_restricted") default boolean isRestricted() {
        return false;
    }

    @Value.Default
    @SerializedName("is_ultra_restricted") default boolean isUltraRestricted() {
        return false;
    }

    @Value.Default
    @SerializedName("is_bot") default boolean isBot() {
        return false;
    }

    @Value.Default
    default PresenceEnum getPresence() {
        return PresenceEnum.UNKNOWN;
    }

    Optional<SlackProfile> getProfile();

    Optional<Map<String, ?>> getPrefs();
}
