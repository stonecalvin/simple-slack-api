package com.ullink.slack.simpleslackapi.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.ullink.slack.simpleslackapi.SlackIntegration;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SessionStatus;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSlackJSONSessionStatusParser
{
    @Test
    public void testParsingSessionDescription() throws Exception
    {
        InputStream stream = getClass().getResourceAsStream("/test_json.json");
        InputStreamReader isReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder strBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            strBuilder.append(line);
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class)) {
            gsonBuilder.registerTypeAdapterFactory(factory);
        }

        Gson gson = gsonBuilder.serializeNulls().create();
        SessionStatus session = gson.fromJson(strBuilder.toString(), SessionStatus.class);

        Map<String, SlackUser> users = session.getSlackUsers().stream()
                .collect(Collectors.toMap(SlackUser::getId, Function.identity()));
        Map<String, SlackIntegration> integrations = session.getIntegrations().stream()
                .collect(Collectors.toMap(SlackIntegration::getId, Function.identity()));
        Map<String, SlackChannel> channels = session.getChannelMap();

        assertThat(channels).containsOnlyKeys("CHANNELID1", "CHANNELID2", "CHANNELID3", "GROUPID1", "DIM01");
        assertThat(users).containsOnlyKeys("USERID1","USERID2","USERID3","USERID4","BOTID1","BOTID2");
        assertThat(session.getUrl()).isEqualTo("wss://mywebsocketurl");

        assertThat(users.get("USERID1").getTimezone().isPresent()).isTrue();
        assertThat(users.get("USERID1").getTimezoneLabel().isPresent()).isTrue();
        assertThat(users.get("USERID1").getTimezoneOffset().isPresent()).isTrue();

        assertThat(users.get("USERID1").getTimezone().get()).isEqualTo("Europe/Amsterdam");
        assertThat(users.get("USERID1").getTimezoneLabel().get()).isEqualTo("Central European Summer Time");
        assertThat(users.get("USERID1").getTimezoneOffset().get()).isEqualTo(7200);

        assertThat(session.getSelf().getId()).isEqualTo("SELF");
        assertThat(session.getSelf().getName()).isEqualTo("myself");

        assertThat(session.getTeam().getId()).isEqualTo("TEAM");
        assertThat(session.getTeam().getName()).isEqualTo("Example Team");
        assertThat(session.getTeam().getDomain()).isEqualTo("example");

        assertThat(integrations.get("INTEGRATION1").getName()).isEqualTo("bot1");
        assertThat(integrations.get("INTEGRATION1").isDeleted()).isEqualTo(false);
        assertThat(integrations.get("INTEGRATION2").getName()).isEqualTo("bot2");
        assertThat(integrations.get("INTEGRATION2").isDeleted()).isEqualTo(true);
    }
}
