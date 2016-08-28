package com.ullink.slack.simpleslackapi.impl;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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

        SlackJSONSessionStatusParser parser = new SlackJSONSessionStatusParser(strBuilder.toString());
        parser.parse();

        assertThat(parser.getChannels()).containsOnlyKeys("CHANNELID1", "CHANNELID2", "CHANNELID3", "GROUPID1", "DIM01");
        assertThat(parser.getUsers()).containsOnlyKeys("USERID1","USERID2","USERID3","USERID4","BOTID1","BOTID2");
        assertThat(parser.getWebSocketURL()).isEqualTo("wss://mywebsocketurl");

        assertThat(parser.getUsers().get("USERID1").tz().isPresent()).isTrue();
        assertThat(parser.getUsers().get("USERID1").tzLabel().isPresent()).isTrue();
        assertThat(parser.getUsers().get("USERID1").tzOffset().isPresent()).isTrue();

        assertThat(parser.getUsers().get("USERID1").tz().get()).isEqualTo("Europe/Amsterdam");
        assertThat(parser.getUsers().get("USERID1").tzLabel().get()).isEqualTo("Central European Summer Time");
        assertThat(parser.getUsers().get("USERID1").tzOffset().get()).isEqualTo(7200);

        assertThat(parser.getSessionPersona().id()).isEqualTo("SELF");
        assertThat(parser.getSessionPersona().name()).isEqualTo("myself");

        assertThat(parser.getTeam().id()).isEqualTo("TEAM");
        assertThat(parser.getTeam().name()).isEqualTo("Example Team");
        assertThat(parser.getTeam().domain()).isEqualTo("example");

        assertThat(parser.getIntegrations().get("INTEGRATION1").name()).isEqualTo("bot1");
        assertThat(parser.getIntegrations().get("INTEGRATION1").deleted()).isEqualTo(false);
        assertThat(parser.getIntegrations().get("INTEGRATION2").name()).isEqualTo("bot2");
        assertThat(parser.getIntegrations().get("INTEGRATION2").deleted()).isEqualTo(true);
    }
}
