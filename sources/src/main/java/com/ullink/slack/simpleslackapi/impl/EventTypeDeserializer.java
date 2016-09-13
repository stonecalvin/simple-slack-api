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
        EVENT_MAP.put(SlackMessageEvent.type, SlackMessagePosted.class);
        EVENT_MAP.put(SlackMessageUpdated.type, SlackMessageUpdated.class);
        EVENT_MAP.put(SlackMessageDeleted.type, SlackMessageDeleted.class);

        EVENT_MAP.put(SlackChannelCreated.type, SlackChannelCreated.class);
        EVENT_MAP.put(SlackChannelArchived.type, SlackChannelArchived.class);
        EVENT_MAP.put(SlackChannelDeleted.type, SlackChannelDeleted.class);
        EVENT_MAP.put(SlackChannelRenamed.type, SlackChannelRenamed.class);
        EVENT_MAP.put(SlackChannelUnarchived.type, SlackChannelUnarchived.class);
        EVENT_MAP.put(SlackChannelJoined.type, SlackChannelJoined.class);
        EVENT_MAP.put(SlackChannelLeft.type, SlackChannelLeft.class);

        EVENT_MAP.put(SlackGroupJoined.type, SlackGroupJoined.class);
        EVENT_MAP.put(ReactionAdded.type, ReactionAdded.class);
        EVENT_MAP.put(ReactionRemoved.type, ReactionRemoved.class);
        EVENT_MAP.put(UserChange.type, UserChange.class);
        EVENT_MAP.put(PresenceChange.type, PresenceChange.class);
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

        if (SlackMessageEvent.type.equals(type)) {
            JsonElement jsonSubtype = jsonObject.get("subtype");

            // Only use a message subtype that we have explicitly added to the map.
            // If this encounters an unregistered subtype, it'll use the default of SlackMessagePosted
            if (jsonSubtype != null && EVENT_MAP.containsKey(jsonSubtype.getAsString())) {
                type = jsonSubtype.getAsString();
            }
        }

        return context.deserialize(json, EVENT_MAP.get(type));
    }
}
