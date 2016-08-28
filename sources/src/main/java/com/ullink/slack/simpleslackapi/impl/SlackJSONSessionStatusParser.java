package com.ullink.slack.simpleslackapi.impl;

import com.ullink.slack.simpleslackapi.json.Channel;
import com.ullink.slack.simpleslackapi.json.Integration;
import com.ullink.slack.simpleslackapi.json.Team;
import com.ullink.slack.simpleslackapi.json.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

class SlackJSONSessionStatusParser {
    private static final Logger             LOGGER       = LoggerFactory.getLogger(SlackJSONSessionStatusParser.class);

    private Map<String, Channel>       channels     = new HashMap<>();
    private Map<String, User>          users        = new HashMap<>();
    private Map<String, Integration>   integrations = new HashMap<>();

    private User sessionPersona;

    private Team team;

    private String                    webSocketURL;

    private String                    toParse;

    private String                    error;

    SlackJSONSessionStatusParser(String toParse)
    {
        this.toParse = toParse;
    }

    Map<String, Channel> getChannels()
    {
        return channels;
    }

    Map<String, User> getUsers()
    {
        return users;
    }

    Map<String,Integration> getIntegrations() {
        return integrations;
    }

    public String getWebSocketURL()
    {
        return webSocketURL;
    }

    public String getError()
    {
        return error;
    }

    void parse() throws ParseException {
        LOGGER.debug("parsing session status : " + toParse);
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(toParse);
        Boolean ok = (Boolean)jsonResponse.get("ok");
        if (Boolean.FALSE.equals(ok)) {
            error = (String)jsonResponse.get("error");
            return;
        }
        JSONArray usersJson = (JSONArray) jsonResponse.get("users");

        for (Object jsonObject : usersJson)
        {
            JSONObject jsonUser = (JSONObject) jsonObject;
            User slackUser = SlackJSONParsingUtils.buildSlackUser(jsonUser);
            LOGGER.debug("slack user found : " + slackUser.id());
            users.put(slackUser.id(), slackUser);
        }

        JSONArray integrationsJson = (JSONArray) jsonResponse.get("bots");
        if (integrationsJson != null) {
            for (Object jsonObject : integrationsJson)
            {
                JSONObject jsonIntegration = (JSONObject) jsonObject;
                Integration slackIntegration = SlackJSONParsingUtils.buildSlackIntegration(jsonIntegration);
                LOGGER.debug("slack integration found : " + slackIntegration.id());
                integrations.put(slackIntegration.id(), slackIntegration);
            }
        }

        JSONArray channelsJson = (JSONArray) jsonResponse.get("channels");

        for (Object jsonObject : channelsJson)
        {
            JSONObject jsonChannel = (JSONObject) jsonObject;
            Channel channel = SlackJSONParsingUtils.buildSlackChannel(jsonChannel, users);
            LOGGER.debug("slack public channel found : " + channel.id());
            channels.put(channel.id(), channel);
        }

        JSONArray groupsJson = (JSONArray) jsonResponse.get("groups");

        for (Object jsonObject : groupsJson)
        {
            JSONObject jsonChannel = (JSONObject) jsonObject;
            Channel channel = SlackJSONParsingUtils.buildSlackChannel(jsonChannel, users);
            LOGGER.debug("slack private group found : " + channel.id());
            channels.put(channel.id(), channel);
        }

        JSONArray imsJson = (JSONArray) jsonResponse.get("ims");

        for (Object jsonObject : imsJson)
        {
            JSONObject jsonChannel = (JSONObject) jsonObject;
            Channel channel = SlackJSONParsingUtils.buildSlackImChannel(jsonChannel, users);
            LOGGER.debug("slack im channel found : " + channel.id());
            channels.put(channel.id(), channel);
        }

        JSONObject selfJson = (JSONObject) jsonResponse.get("self");
        sessionPersona = SlackJSONParsingUtils.buildSlackUser(selfJson);

        JSONObject teamJson = (JSONObject) jsonResponse.get("team");
        team = SlackJSONParsingUtils.buildSlackTeam(teamJson);

        webSocketURL = (String) jsonResponse.get("url");

    }

    public User getSessionPersona()
    {
        return sessionPersona;
    }

    public Team getTeam()
    {
        return team;
    }
}
