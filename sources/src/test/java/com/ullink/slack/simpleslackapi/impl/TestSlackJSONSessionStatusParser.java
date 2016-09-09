package com.ullink.slack.simpleslackapi.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.ullink.slack.simpleslackapi.json.Channel;
import com.ullink.slack.simpleslackapi.json.Integration;
import com.ullink.slack.simpleslackapi.json.SessionStatus;
import com.ullink.slack.simpleslackapi.json.User;
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

        Map<String, User> users = session.users().stream()
                .collect(Collectors.toMap(User::id, Function.identity()));
        Map<String, Integration> integrations = session.bots().stream()
                .collect(Collectors.toMap(Integration::id, Function.identity()));
        Map<String, Channel> channels = session.channelMap();

        assertThat(channels).containsOnlyKeys("CHANNELID1", "CHANNELID2", "CHANNELID3", "GROUPID1", "DIM01");
        assertThat(users).containsOnlyKeys("USERID1","USERID2","USERID3","USERID4","BOTID1","BOTID2");
        assertThat(session.url()).isEqualTo("wss://mywebsocketurl");

        assertThat(users.get("USERID1").tz().isPresent()).isTrue();
        assertThat(users.get("USERID1").tzLabel().isPresent()).isTrue();
        assertThat(users.get("USERID1").tzOffset().isPresent()).isTrue();

        assertThat(users.get("USERID1").tz().get()).isEqualTo("Europe/Amsterdam");
        assertThat(users.get("USERID1").tzLabel().get()).isEqualTo("Central European Summer Time");
        assertThat(users.get("USERID1").tzOffset().get()).isEqualTo(7200);

        assertThat(session.self().id()).isEqualTo("SELF");
        assertThat(session.self().name()).isEqualTo("myself");

        assertThat(session.team().id()).isEqualTo("TEAM");
        assertThat(session.team().name()).isEqualTo("Example Team");
        assertThat(session.team().domain()).isEqualTo("example");

        assertThat(integrations.get("INTEGRATION1").name()).isEqualTo("bot1");
        assertThat(integrations.get("INTEGRATION1").deleted()).isEqualTo(false);
        assertThat(integrations.get("INTEGRATION2").name()).isEqualTo("bot2");
        assertThat(integrations.get("INTEGRATION2").deleted()).isEqualTo(true);
    }
}
