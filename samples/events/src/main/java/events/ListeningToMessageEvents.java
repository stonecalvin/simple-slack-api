package events;

import com.google.common.eventbus.Subscribe;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import java.util.Optional;


/**
 * Samples showing how to listen to message events
 */
public class ListeningToMessageEvents
{
    private class MessagePostedListener {

        @Subscribe
        public void listenMessagePosted(SlackMessagePosted event) {
            SlackChannel channelOnWhichMessageWasPosted = event.getChannel();
            String messageContent = event.getText();
            SlackUser messageSender = event.getSender();
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
     * This method demonstrate what is available in a SlackMessagePosted event
     */
    public void slackMessagePostedEventContent(SlackSession session) {
        session.registerListener(new MessagePostedListener() {
            @Subscribe
            public void listenMessagePosted(SlackMessagePosted event) {
                // if I'm only interested on a certain channel :
                // I can filter out messages coming from other channels
                SlackSession session1 = event.getSlackSession();
                Optional<SlackChannel> theChannel = session1.findChannelByName("thechannel");

                if (!theChannel.isPresent()
                    || !theChannel.get().getId().equals(event.getChannel().getId())) {
                    return;
                }

                // if I'm only interested on messages posted by a certain user :
                // I can filter out messages coming from other users
                Optional<SlackUser> myInterestingUser = session1.findUserByUserName("gueststar");

                if (!myInterestingUser.isPresent()
                    || !myInterestingUser.get().getId().equals(event.getSender().getId())) {
                    return;
                }

                // How to avoid message the bot send (yes it is receiving notification for its own messages)
                // session.sessionPersona() returns the user this session represents
                if (session1.sessionPersona().getId().equals(event.getSender().getId())) {
                    return;
                }

                // Then you can also filter out on the message content itself
                String messageContent = event.getText();
                if (!messageContent.contains("keyword")) {
                    return;
                }

                // once you've defined that the bot needs to react you can use the session to do that :
                session1.sendMessage(event.getChannel(), "SlackMessage with keyword was sent by the expected user on this channel!");
            }
        });
    }
}
