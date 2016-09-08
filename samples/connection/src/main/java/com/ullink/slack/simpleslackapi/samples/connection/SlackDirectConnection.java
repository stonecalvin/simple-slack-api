package com.ullink.slack.simpleslackapi.samples.connection;

import com.google.common.eventbus.Subscribe;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.events.Connected;
import com.ullink.slack.simpleslackapi.events.MessagePosted;

import java.io.IOException;


class TestListener {
    String username;
    @Subscribe
    public void listenConnected(Connected event) {
        username = event.slackPersona().name();
        System.out.println("USER HAS CONNECTED: " + username);
    }

    @Subscribe
    public void listenMessagePosted(MessagePosted event) {
        System.out.println("MESSAGE POSTED: " + event.text());
        System.out.println("SENDER: " + event.sender());
    }

}


class EchoListener {
    String botName;

    EchoListener(String botName) {
        this.botName = botName;
    }

    @Subscribe
    public void listenMessagePosted(MessagePosted event) {
        // Echo the message back if its not our own message
        if (!botName.equals(event.sender().name())) {
            event.slackSession().sendMessage(event.channel(), event.text());
        }
    }

}

/**
 * This sample code is creating a Slack session and is connecting to slack. To get some more details on
 * how to get a token, please have a look here : https://api.slack.com/bot-users
 */
public class SlackDirectConnection
{
    public static void main(String[] args) throws IOException
    {
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession("xoxb-73201034784-qkqGJqSxwEa7HIE9jnd1spzn");
        session.connect();
        session.registerListener(new TestListener());

        // Talk to me, botty
        SlackSession echoBotSession = SlackSessionFactory.createWebSocketSlackSession("xoxb-77386972404-64hgUdUUwJYIBVftRQy0Tx5G");
        echoBotSession.connect();
        echoBotSession.registerListener(new EchoListener(echoBotSession.sessionPersona().name()));
    }
}
