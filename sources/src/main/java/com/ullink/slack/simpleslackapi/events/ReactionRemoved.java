package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackUser;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface ReactionRemoved extends SlackEvent{
    SlackChannel channel();
    SlackUser user();
    String messageID();
    String emojiName();

    Optional<String> fileID();
    Optional<String> fileCommentID();
}
