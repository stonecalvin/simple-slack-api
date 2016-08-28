package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.json.Channel;
import com.ullink.slack.simpleslackapi.replies.SlackChannelReply;
import org.json.simple.JSONObject;

public class SlackChannelReplyImpl extends SlackReplyImpl implements SlackChannelReply
{
    private Channel slackChannel;

    SlackChannelReplyImpl(boolean ok, String error, JSONObject plain, Channel slackChannel)
    {
        super(ok,error);
        this.slackChannel = slackChannel;
    }

    @Override
    public Channel getSlackChannel()
    {
        return slackChannel;
    }
}
