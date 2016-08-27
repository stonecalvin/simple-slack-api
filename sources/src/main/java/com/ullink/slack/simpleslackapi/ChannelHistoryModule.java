package com.ullink.slack.simpleslackapi;

import com.ullink.slack.simpleslackapi.events.MessagePosted;
import org.threeten.bp.LocalDate;

import java.util.List;

public interface ChannelHistoryModule {

    List<MessagePosted> fetchHistoryOfChannel(String channelName);

    List<MessagePosted> fetchHistoryOfChannel(String channelName, LocalDate day);

    List<MessagePosted> fetchHistoryOfChannel(String channelName, int numberOfMessages);

    List<MessagePosted> fetchHistoryOfChannel(String channelName, LocalDate day, int numberOfMessages);

    List<MessagePosted> fetchUpdatingHistoryOfChannel(String channelId);

    List<MessagePosted> fetchUpdatingHistoryOfChannel(String channelId, LocalDate day);

    List<MessagePosted> fetchUpdatingHistoryOfChannel(String channelId, int numberOfMessages);

    List<MessagePosted> fetchUpdatingHistoryOfChannel(String channelId, LocalDate day, int numberOfMessages);


}
