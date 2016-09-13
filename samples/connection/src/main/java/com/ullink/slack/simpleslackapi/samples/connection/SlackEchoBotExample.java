package com.ullink.slack.simpleslackapi.samples.connection;

import com.google.common.eventbus.Subscribe;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import java.io.IOException;


/**
 * Example of a bot that echos messages back to the chat.
 */
class EchoListener {
    private String botName;

    EchoListener(String botName) {
        this.botName = botName;
    }

    @Subscribe
    public void listenMessagePosted(SlackMessagePosted event) {
        System.out.println("MESSAGE POSTED: " + event.getText());
        System.out.println("SENDER: " + event.getSender());

        // Echo the message back if its not our own message
        if (!botName.equals(event.getSender().getName())) {
            event.getSlackSession().sendMessage(event.getChannel(), event.getText());
        }
    }

}

/**
 * This sample code demonstrates how to set up a listener that allows the bot to easily echo messages back to the
 * chat room.
 */
public class SlackEchoBotExample
{
    public static void main(String[] args) throws IOException
    {
        SlackSession echoBotSession = SlackSessionFactory.createWebSocketSlackSession("my-bot-auth-token");
        echoBotSession.connect();
        echoBotSession.registerListener(new EchoListener(echoBotSession.sessionPersona().getName()));
    }
}
