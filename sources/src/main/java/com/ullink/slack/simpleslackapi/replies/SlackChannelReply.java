package com.ullink.slack.simpleslackapi.replies;

import com.ullink.slack.simpleslackapi.json.Channel;

public interface SlackChannelReply extends ParsedSlackReply {
    Channel getSlackChannel();
}
