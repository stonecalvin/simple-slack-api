package com.ullink.slack.simpleslackapi.json;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

/**
 {
 "id":"C25409YF5",
 "name":"general",
 "created":1472170908,
 "creator":"U255XRF7U",
 "is_channel":true,
 "is_archived":false,
 "is_general":true,
 "has_pins":false,
 "is_member":false
 },

 */

@Gson.TypeAdapters
@Value.Immutable
public interface Channel {
    // TODO: Should this be split up into different types of channels?
    // Original version was merging them all together, but this is really channel + group + ims
    // Splitting up should help with the defaulting / optionals

    String id();

    Optional<Long> created();
    Optional<String> name();
    Optional<String> creator();
    Optional<Topic> topic();
    Optional<Topic> purpose();

    @Value.Default
    @SerializedName("has_pins") default boolean hasPins() {
        return false;
    };

    @Value.Default
    @SerializedName("is_channel") default boolean isChannel() {
        return false;
    }

    @Value.Default
    @SerializedName("is_archived") default boolean isArchived() {
        return false;
    }
    @Value.Default
    @SerializedName("is_general") default boolean isGeneral() {
        return false;
    }
    @Value.Default
    @SerializedName("is_member") default boolean isMember() {
        return false;
    }

    // Used by IM channels
    @Value.Default
    @SerializedName("is_im") default boolean isIm() {
        return false;
    }

    Optional<String> user();

    @Value.Default
    default ChannelType type() {
        //that's a bit hacky
        if (this.isIm()) {
            return ChannelType.INSTANT_MESSAGING;
        }
        if (this.id().startsWith("G")) {
            return ChannelType.PRIVATE_GROUP;
        }
        return ChannelType.PUBLIC_CHANNEL;
    }

    enum ChannelType {
        PUBLIC_CHANNEL, PRIVATE_GROUP, INSTANT_MESSAGING
    }
}
