package com.ullink.slack.simpleslackapi.impl;

import com.google.gson.*;
import com.ullink.slack.simpleslackapi.events.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Required to deserialize events by their type attribute.
 */
public class EventTypeDeserializer implements JsonDeserializer<SlackEvent> {
    private final static Map<String, Class<?>> EVENT_MAP = new HashMap<>();
    static {
        EVENT_MAP.put(MessageEvent.type, MessagePosted.class);
        EVENT_MAP.put(MessageUpdated.type, MessageUpdated.class);
        EVENT_MAP.put(MessageDeleted.type, MessageDeleted.class);

        EVENT_MAP.put(ChannelCreated.type, ChannelCreated.class);
        EVENT_MAP.put(ChannelArchived.type, ChannelArchived.class);
        EVENT_MAP.put(ChannelDeleted.type, ChannelDeleted.class);
        EVENT_MAP.put(ChannelRenamed.type, ChannelRenamed.class);
        EVENT_MAP.put(ChannelUnarchived.type, ChannelUnarchived.class);
        EVENT_MAP.put(ChannelJoined.type, ChannelJoined.class);
        EVENT_MAP.put(ChannelLeft.type, ChannelLeft.class);

        EVENT_MAP.put(GroupJoined.type, GroupJoined.class);
        EVENT_MAP.put(ReactionAdded.type, ReactionAdded.class);
        EVENT_MAP.put(ReactionRemoved.type, ReactionRemoved.class);
        EVENT_MAP.put(UserChange.type, UserChange.class);
        EVENT_MAP.put(PresenceChanged.type, PresenceChanged.class);
        EVENT_MAP.put(PinAdded.type, PinAdded.class);
        EVENT_MAP.put(PinRemoved.type, PinRemoved.class);
    }

    @Override
    public SlackEvent deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement jsonType = jsonObject.get("type");
        String type = jsonType.getAsString();

        if (type == null || !EVENT_MAP.containsKey(type)) {
            return ImmutableUnknownEvent.builder().build();
        }

        if (MessageEvent.type.equals(type)) {
            JsonElement jsonSubtype = jsonObject.get("subtype");

            // Only use a message subtype that we have explicitly added to the map.
            // If this encounters an unregistered subtype, it'll use the default of MessagePosted
            if (jsonSubtype != null && EVENT_MAP.containsKey(jsonSubtype.getAsString())) {
                type = jsonSubtype.getAsString();
            }
        }

        return context.deserialize(json, EVENT_MAP.get(type));
    }
}
