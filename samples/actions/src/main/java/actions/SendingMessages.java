package actions;

import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.replies.SlackParsedReply;

import java.util.Optional;

/**
 * This sample code is showing how to send some messages assuming you already have a SlackSession
 */
public class SendingMessages
{

    /**
     * This method shows how to send a message to a given channel (public channel, private group or direct message channel)
     */
    public void sendMessageToAChannel(SlackSession session)
    {

        //get a channel
        Optional<SlackChannel> channel = session.findChannelByName("achannel");

        if (channel.isPresent()) {
            session.sendMessage(channel.get(), "Hey there");
        }
    }

    /**
     * This method shows how to send a direct message to a user
     */
    public void sendDirectMessageToAUser(SlackSession session)
    {

        //get a user
        Optional<SlackUser> user = session.findUserByUserName("killroy");

        if (!user.isPresent()) {
            // Could not find a user with the given name, do some sort of error handling
            return;
        }

        session.sendMessageToUser(user.get(), "Hi, how are you", null);
    }

    /**
     * This method shows how to send a direct message to a user, but this time it shows how it can be done using the
     * direct message channels
     */
    public void sendDirectMessageToAUserTheHardWay(SlackSession session)
    {

        //get a user
        Optional<SlackUser> user = session.findUserByUserName("killroy");

        if (!user.isPresent()) {
            // Could not find a user with the given name, do some sort of error handling
            return;
        }

        //get its direct message channel
        SlackMessageHandle<SlackParsedReply> reply = session.openDirectMessageChannel(user.get());

        //get the channel
        SlackChannel channel = reply.getReply().getSlackChannel().get();

        //send the message to this channel
        session.sendMessage(channel, "Hi, how are you", null);
    }

    /**
     * This method shows how to send a direct message to multiple users.
     */
    public void sendDirectMessageToMultipleUsers(SlackSession session)
    {
        //get some users
        // For this example, we will ignore the fact that these users may not actually exist.
        SlackUser killroy = session.findUserByUserName("killroy").get();
        SlackUser janedoe = session.findUserByUserName("janedoe").get();
        SlackUser agentsmith = session.findUserByUserName("agentsmith").get();

        //open a multiparty direct message channel between the bot and these users
        SlackMessageHandle<SlackParsedReply> reply = session.openMultipartyDirectMessageChannel(killroy, janedoe, agentsmith);

        //get the channel
        SlackChannel channel = reply.getReply().getSlackChannel().get();

        //send the message to this channel
        session.sendMessage(channel, "Hi, how are you guys", null);
    }

    /**
     * This method shows how to send a message using the PreparedMessage builder (allows for multiple attachments)
     */
    public void sendUsingPreparedMessage(SlackSession session)
    {
        //get a channel
        Optional<SlackChannel> channel = session.findChannelByName("achannel");
        if (!channel.isPresent()) {
            // We were unable to find a channel with the given name.
            // Some sort of error handling happens here
            return;
        }

        //build a message object
        SlackPreparedMessage preparedMessage = ImmutableSlackPreparedMessage.builder()
                .message("Hey, this is a message")
                .isUnfurl(true)
                .build();

        session.sendMessage(channel.get(), preparedMessage);
    }
}
