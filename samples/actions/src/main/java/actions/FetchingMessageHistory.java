package actions;

import com.ullink.slack.simpleslackapi.ChannelHistoryModule;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.MessagePosted;
import com.ullink.slack.simpleslackapi.impl.ChannelHistoryModuleFactory;
import com.ullink.slack.simpleslackapi.json.Channel;
import org.threeten.bp.LocalDate;

import java.util.List;

/**
 * This sample code is showing various ways on how to query a channel message history assuming you already have a SlackSession
 */
public class FetchingMessageHistory
{
    /**
     * This method how to get the message history from a given channel (by default, 1000 max messages are fetched)
     */
    public void fetchSomeMessagesFromChannelHistory(SlackSession session, Channel slackChannel)
    {
        //build a channelHistory module from the slack session
        ChannelHistoryModule channelHistoryModule = ChannelHistoryModuleFactory.createChannelHistoryModule(session);

        List<MessagePosted> messages = channelHistoryModule.fetchHistoryOfChannel(slackChannel.id());
    }

    /**
     * This method how to get the 10 last messages from the message history of a given channel
     */
    public void fetchTenLastMessagesFromChannelHistory(SlackSession session, Channel slackChannel)
    {
        //build a channelHistory module from the slack session
        ChannelHistoryModule channelHistoryModule = ChannelHistoryModuleFactory.createChannelHistoryModule(session);

        List<MessagePosted> messages = channelHistoryModule.fetchHistoryOfChannel(slackChannel.id(),10);
    }

    /**
     * This method how to get the message history on a given date from a given channel (by default, 1000 max messages are fetched)
     */
    public void fetchMessagesOfGivenDateFromChannelHistory(SlackSession session, Channel slackChannel, LocalDate date)
    {
        //build a channelHistory module from the slack session
        ChannelHistoryModule channelHistoryModule = ChannelHistoryModuleFactory.createChannelHistoryModule(session);

        List<MessagePosted> messages = channelHistoryModule.fetchHistoryOfChannel(slackChannel.id(),date);
    }

    /**
     * This method how to get the 10 last message of a given date from the message history of a given channel
     */
    public void fetchTenLastMessagesOfGivenDateFromChannelHistory(SlackSession session, Channel slackChannel, LocalDate date)
    {
        //build a channelHistory module from the slack session
        ChannelHistoryModule channelHistoryModule = ChannelHistoryModuleFactory.createChannelHistoryModule(session);

        List<MessagePosted> messages = channelHistoryModule.fetchHistoryOfChannel(slackChannel.id(),date,10);
    }

}
