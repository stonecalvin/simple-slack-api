package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.json.Channel;
import com.ullink.slack.simpleslackapi.json.User;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface ReactionAdded extends SlackEvent{
    Channel channel();
    User user();
    String messageID();
    String emojiName();

    Optional<String> fileID();
    Optional<String> fileCommentID();
}
