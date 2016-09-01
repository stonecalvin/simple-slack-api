package events;

import com.google.common.eventbus.Subscribe;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.MessagePosted;
import com.ullink.slack.simpleslackapi.json.Channel;
import com.ullink.slack.simpleslackapi.json.User;

import java.util.Optional;


/**
 * Samples showing how to listen to message events
 */
public class ListeningToMessageEvents
{
    private class MessagePostedListener {

        @Subscribe
        public void listenMessagePosted(MessagePosted event) {
            Channel channelOnWhichMessageWasPosted = event.channel();
            String messageContent = event.text();
            User messageSender = event.sender();
        }

    }

    /**
     * This method shows how to register a listener on a SlackSession
     */
    public void registeringAListener(SlackSession session)
    {
        //add it to the session
        session.registerListener(new MessagePostedListener());

        //that's it, the listener will get every message post events the bot can get notified on
        //(IE: the messages sent on channels it joined or sent directly to it)
    }

    /**
     * This method demonstrate what is available in a MessagePosted event
     */
    public void slackMessagePostedEventContent(SlackSession session) {
        session.registerListener(new MessagePostedListener() {
            @Subscribe
            public void listenMessagePosted(MessagePosted event) {
                // if I'm only interested on a certain channel :
                // I can filter out messages coming from other channels
                SlackSession session1 = event.slackSession();
                Optional<Channel> theChannel = session1.findChannelByName("thechannel");

                if (!theChannel.isPresent()
                    || !theChannel.get().id().equals(event.channel().id())) {
                    return;
                }

                // if I'm only interested on messages posted by a certain user :
                // I can filter out messages coming from other users
                Optional<User> myInterestingUser = session1.findUserByUserName("gueststar");

                if (!myInterestingUser.isPresent()
                    || !myInterestingUser.get().id().equals(event.sender().id())) {
                    return;
                }

                // How to avoid message the bot send (yes it is receiving notification for its own messages)
                // session.sessionPersona() returns the user this session represents
                if (session1.sessionPersona().id().equals(event.sender().id())) {
                    return;
                }

                // Then you can also filter out on the message content itself
                String messageContent = event.text();
                if (!messageContent.contains("keyword")) {
                    return;
                }

                // once you've defined that the bot needs to react you can use the session to do that :
                session1.sendMessage(event.channel(), "Message with keyword was sent by the expected user on this channel!");
            }
        });
    }
}
