package com.ullink.slack.simpleslackapi.impl;

import com.google.common.io.CharStreams;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.events.*;
import com.ullink.slack.simpleslackapi.impl.SlackChatConfiguration.Avatar;
import com.ullink.slack.simpleslackapi.replies.GenericSlackReply;
import com.ullink.slack.simpleslackapi.replies.ImmutableGenericSlackReply;
import com.ullink.slack.simpleslackapi.replies.ImmutableSlackParsedReply;
import com.ullink.slack.simpleslackapi.replies.SlackParsedReply;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

class SlackWebSocketSessionImpl extends AbstractSlackSessionImpl implements SlackSession, MessageHandler.Whole<String> {
    private static final String SLACK_API_SCHEME = "https";
    private static final String SLACK_API_HOST = "slack.com";
    private static final String SLACK_API_PATH = "/api";
    private static final String SLACK_API_HTTPS_ROOT = SLACK_API_SCHEME + "://" + SLACK_API_HOST + SLACK_API_PATH + "/";
    private static final String DIRECT_MESSAGE_OPEN_CHANNEL_COMMAND = "im.open";
    private static final String MULTIPARTY_DIRECT_MESSAGE_OPEN_CHANNEL_COMMAND = "mpim.open";
    private static final String CHANNELS_LEAVE_COMMAND = "channels.leave";
    private static final String CHANNELS_JOIN_COMMAND = "channels.join";
    private static final String CHANNELS_SET_TOPIC_COMMAND = "channels.setTopic";
    private static final String CHANNELS_INVITE_COMMAND = "channels.invite";
    private static final String CHANNELS_ARCHIVE_COMMAND = "channels.archive";
    private static final String CHAT_POST_MESSAGE_COMMAND = "chat.postMessage";
    private static final String FILE_UPLOAD_COMMAND = "files.upload";
    private static final String CHAT_DELETE_COMMAND = "chat.delete";
    private static final String CHAT_UPDATE_COMMAND = "chat.update";
    private static final String REACTIONS_ADD_COMMAND = "reactions.add";
    private static final String INVITE_USER_COMMAND = "users.admin.invite";
    private static final String SET_PERSONA_ACTIVE = "users.setPresence";
    private static final String LIST_EMOJI_COMMAND = "emoji.list";
    private static final String LIST_USERS = "users.list";

    private static final Logger LOGGER = LoggerFactory.getLogger(SlackWebSocketSessionImpl.class);
    private static final String SLACK_HTTPS_AUTH_URL = "https://slack.com/api/rtm.start?token=";

    private static final int DEFAULT_HEARTBEAT_IN_MILLIS = 30000;

    private Session websocketSession;
    private String authToken;
    private String proxyAddress;
    private int proxyPort = -1;
    private HttpHost proxyHost;
    private long lastPingSent;
    private volatile long lastPingAck;

    private AtomicLong messageId = new AtomicLong();

    private boolean reconnectOnDisconnection;
    private boolean wantDisconnect;

    private Thread connectionMonitoringThread;
    private long heartbeat;


    // -----------------------------------------------------------------------------------------------------------------
    // ----- SESSION MANAGEMENT ----------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    SlackWebSocketSessionImpl(String authToken, boolean reconnectOnDisconnection, long heartbeat, TimeUnit unit) {
        this.authToken = authToken;
        this.reconnectOnDisconnection = reconnectOnDisconnection;
        this.heartbeat = heartbeat != 0 ? unit.toMillis(heartbeat) : DEFAULT_HEARTBEAT_IN_MILLIS;
    }

    SlackWebSocketSessionImpl(String authToken, Proxy.Type proxyType, String proxyAddress, int proxyPort, boolean reconnectOnDisconnection, long heartbeat, TimeUnit unit) {
        this.authToken = authToken;
        this.proxyAddress = proxyAddress;
        this.proxyPort = proxyPort;
        this.proxyHost = new HttpHost(proxyAddress, proxyPort);
        this.reconnectOnDisconnection = reconnectOnDisconnection;
        this.heartbeat = heartbeat != 0 ? unit.toMillis(heartbeat) : DEFAULT_HEARTBEAT_IN_MILLIS;
    }

    @Override
    public void connect() throws IOException {
        wantDisconnect = false;
        connectImpl();
        LOGGER.debug("starting actions monitoring");
        startConnectionMonitoring();
    }

    @Override
    public void disconnect() {
        wantDisconnect = true;
        LOGGER.debug("Disconnecting from the Slack server");
        disconnectImpl();
        stopConnectionMonitoring();
    }
    @Override
    public boolean isConnected()
    {
        return websocketSession != null && websocketSession.isOpen();
    }

    private void connectImpl() throws IOException {
        LOGGER.info("connecting to slack");

        // Reset some members
        lastPingSent = 0;
        lastPingAck = 0;

        // Make the request to the slack API
        HttpClient httpClient = getHttpClient();
        HttpGet request = new HttpGet(SLACK_HTTPS_AUTH_URL + authToken);
        HttpResponse response;
        response = httpClient.execute(request);
        LOGGER.debug(response.getStatusLine().toString());

        // Do some parsing of the response
        String jsonResponse = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));

        GsonBuilder gsonBuilder = new GsonBuilder();
        for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class)) {
            gsonBuilder.registerTypeAdapterFactory(factory);
        }

        gson = gsonBuilder
                .registerTypeAdapter(SlackEvent.class, new EventTypeDeserializer())
                .serializeNulls()
                .create();
        SessionStatus session = gson.fromJson(jsonResponse, SessionStatus.class);

        users = session.getSlackUsers().stream()
                .collect(Collectors.toMap(SlackUser::getId, Function.identity()));
        integrations = session.getIntegrations().stream()
                .collect(Collectors.toMap(SlackIntegration::getId, Function.identity()));
        channels = session.getChannelMap();

        sessionPersona = session.getSelf();
        slackTeam = session.getTeam();
        LOGGER.info("SlackTeam " + slackTeam.getId() + " : " + slackTeam.getName());
        LOGGER.info("Self " + sessionPersona.getId() + " : " + sessionPersona.getName());
        LOGGER.info(users.size() + " users found on this session");
        LOGGER.info(channels.size() + " channels found on this session");
        String wssurl = session.getUrl();
        LOGGER.debug("retrieved websocket URL : " + wssurl);



        // This seems to be a new branch of logic.
        // Creating a client manager...
        ClientManager client = ClientManager.createClient();

        if (proxyAddress != null) {
            client.getProperties().put(ClientProperties.PROXY_URI, "http://" + proxyAddress + ":" + proxyPort);
        }
        final MessageHandler handler = this;
        LOGGER.debug("initiating actions to websocket");

        // This seems to be establishing a websocket connect.
        try {
            websocketSession = client.connectToServer(new Endpoint()
            {
                @Override
                public void onOpen(Session session, EndpointConfig config)
                {
                    session.addMessageHandler(handler);
                }

            }, URI.create(wssurl));
        }
        catch (DeploymentException e) {
            LOGGER.error(e.toString());
        }

        // If we have successfully established a connection...
        if (websocketSession != null) {
            eventBus.post(ImmutableSlackConnected.builder()
                    .slackPersona(sessionPersona)
                    .slackSession(SlackWebSocketSessionImpl.this)
                    .build());

            LOGGER.debug("websocket actions established");
            LOGGER.info("slack session ready");
        }
    }

    private void disconnectImpl() {
        if (websocketSession != null) {
            try {
                websocketSession.close();
            }
            catch (IOException ex) {
                // ignored.
            }
            finally {
                eventBus.post(ImmutableSlackDisconnected.builder()
                        .slackPersona(sessionPersona)
                        .slackSession(SlackWebSocketSessionImpl.this)
                        .build());
                websocketSession = null;
            }
        }
    }

    // This method has no Slack stuff, leave it be for now.
    private void startConnectionMonitoring() {
        connectionMonitoringThread = new Thread() {
            @Override
            public void run() {
                LOGGER.debug("monitoring thread started");
                while (true) {
                    try {
                        // heart beat of 30s (should be configurable in the future)
                        Thread.sleep(heartbeat);

                        // disconnect() was called.
                        if (wantDisconnect) {
                            this.interrupt();
                        }

                        if (lastPingSent != lastPingAck || websocketSession == null) {
                            // disconnection happened
                            LOGGER.warn("Connection lost...");
                            try {
                                if (websocketSession != null)
                                {
                                    websocketSession.close();
                                }
                            }
                            catch (IOException e) {
                                LOGGER.error("exception while trying to close the websocket ", e);
                            }
                            websocketSession = null;
                            if (reconnectOnDisconnection) {
                                connectImpl();
                            }
                            else {
                                this.interrupt();
                            }
                        }
                        else {
                            lastPingSent = getNextMessageId();
                            LOGGER.debug("sending ping " + lastPingSent);
                            try {
                                if (websocketSession.isOpen()) {
                                    websocketSession.getBasicRemote().sendText("{\"type\":\"ping\",\"id\":" + lastPingSent + "}");
                                }
                                else if (reconnectOnDisconnection) {
                                    connectImpl();
                                }
                            }
                            catch (IllegalStateException e) {
                                // websocketSession might be closed in this case
                                if (reconnectOnDisconnection) {
                                    connectImpl();
                                }
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        break;
                    }
                    catch (IOException e) {
                        LOGGER.error("unexpected exception on monitoring thread ", e);
                    }
                }
                LOGGER.debug("monitoring thread stopped");
            }
        };

        if (!wantDisconnect) {
            connectionMonitoringThread.start();
        }
    }

    private void stopConnectionMonitoring() {
        if (connectionMonitoringThread != null) {
            while (true) {
                try {
                    connectionMonitoringThread.interrupt();
                    connectionMonitoringThread.join();
                    break;
                }
                catch (InterruptedException ex) {
                    // ouch - let's try again!
                }
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // ----- API STARTS HERE -------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    private List<SlackChannel> getAllIMChannels() {
        return getChannels().stream()
                .filter(SlackChannel::isIm)
                .collect(Collectors.toList());
    }

    private SlackChannel getIMChannelForUser(SlackUser slackUser) {
        SlackMessageHandle<SlackParsedReply> reply = openDirectMessageChannel(slackUser);

        return getAllIMChannels().stream()
                .filter(channel -> channel.getUser().isPresent())
                .filter(channel -> channel.getUser().get().equals(slackUser.getId()))
                .findFirst()
                .orElse(reply.getReply().getSlackChannel().get());
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessageToUser(SlackUser slackUser, String message, SlackAttachment attachment) {
        SlackChannel iMChannel = getIMChannelForUser(slackUser);
        return sendMessage(iMChannel, message, attachment, DEFAULT_CONFIGURATION);
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessageToUser(String userName, String message, SlackAttachment attachment) {
        Optional<SlackUser> user = findUserByUserName(userName);

        if (!user.isPresent()) {
            // TODO: Error handling sweep.
            // We couldnt find a user with the given name,
            //raise some sort of error here
            return null;
        }

        return sendMessageToUser(user.get(), message, attachment);
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessage(SlackChannel channel, SlackPreparedMessage preparedMessage, SlackChatConfiguration chatConfiguration) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("channel", channel.getId());
        arguments.put("text", preparedMessage.getMessage());
        if (chatConfiguration.asUser)
        {
            arguments.put("as_user", "true");
        }
        if (chatConfiguration.avatar == Avatar.ICON_URL)
        {
            arguments.put("icon_url", chatConfiguration.avatarDescription);
        }
        if (chatConfiguration.avatar == Avatar.EMOJI)
        {
            arguments.put("icon_emoji", chatConfiguration.avatarDescription);
        }
        if (chatConfiguration.userName != null)
        {
            arguments.put("username", chatConfiguration.userName);
        }
        if (preparedMessage.getAttachments().isPresent())
        {
            arguments.put("attachments", gson.toJson(preparedMessage.getAttachments().get(),
                    new TypeToken<ArrayList<SlackAttachment>>() {}.getType()));
        }
        if (preparedMessage.isUnfurl().isPresent() && !preparedMessage.isUnfurl().get())
        {
            arguments.put("unfurl_links", "false");
            arguments.put("unfurl_media", "false");
        }
        if (preparedMessage.isLinkNames().isPresent() && preparedMessage.isLinkNames().get())
        {
            arguments.put("link_names", "1");
        }

        postSlackCommand(arguments, CHAT_POST_MESSAGE_COMMAND, handle);
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendFile(SlackChannel channel, byte[] data, String fileName) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("channels", channel.getId());
        arguments.put("filename", fileName);
        postSlackCommandWithFile(arguments, data, fileName,FILE_UPLOAD_COMMAND, handle);
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> deleteMessage(String timestamp, SlackChannel channel) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("channel", channel.getId());
        arguments.put("ts", timestamp);
        postSlackCommand(arguments, CHAT_DELETE_COMMAND, handle);
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> updateMessage(String timestamp, SlackChannel channel, String message) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("ts", timestamp);
        arguments.put("channel", channel.getId());
        arguments.put("text", message);
        postSlackCommand(arguments, CHAT_UPDATE_COMMAND, handle);
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> addReactionToMessage(SlackChannel channel, String messageTimeStamp, String emojiCode) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("channel", channel.getId());
        arguments.put("ts", messageTimeStamp);
        arguments.put("name", emojiCode);
        postSlackCommand(arguments, REACTIONS_ADD_COMMAND, handle);
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> joinChannel(String channelName) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("name", channelName);
        postSlackCommand(arguments, CHANNELS_JOIN_COMMAND, handle);
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> setChannelTopic(SlackChannel channel, String topic) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("channel", channel.getId());
        arguments.put("topic", topic);
        postSlackCommand(arguments, CHANNELS_SET_TOPIC_COMMAND, handle);
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> leaveChannel(SlackChannel channel) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("channel", channel.getId());
        postSlackCommand(arguments, CHANNELS_LEAVE_COMMAND, handle);
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> inviteToChannel(SlackChannel channel, SlackUser slackUser) {
      SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
      Map<String, String> arguments = new HashMap<>();
      arguments.put("token", authToken);
      arguments.put("channel", channel.getId());
      arguments.put("user", slackUser.getId());
      postSlackCommand(arguments, CHANNELS_INVITE_COMMAND, handle);
      return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> archiveChannel(SlackChannel channel) {
      SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
      Map<String, String> arguments = new HashMap<>();
      arguments.put("token", authToken);
      arguments.put("channel", channel.getId());
      postSlackCommand(arguments, CHANNELS_ARCHIVE_COMMAND, handle);
      return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> openDirectMessageChannel(SlackUser slackUser) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("user", slackUser.getId());
        postSlackCommand(arguments, DIRECT_MESSAGE_OPEN_CHANNEL_COMMAND, handle);
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> openMultipartyDirectMessageChannel(SlackUser... slackUsers) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < slackUsers.length ; i++) {
            if (i != 0) {
                strBuilder.append(',');
            }
            strBuilder.append(slackUsers[i].getId());
        }
        arguments.put("users", strBuilder.toString());
        postSlackCommand(arguments, MULTIPARTY_DIRECT_MESSAGE_OPEN_CHANNEL_COMMAND, handle);
        if (!handle.getReply().isOk()) {
            LOGGER.debug("Error occurred while performing command: '" + handle.getReply().getErrorMessage().get() + "'");
            return null;
        }
        return handle;
    }

    public SlackMessageHandle<SlackParsedReply> listEmoji() {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        postSlackCommand(arguments, LIST_EMOJI_COMMAND, handle);
        return handle;
    }

    @Override
    public void refetchUsers() {
        Map<String, String> params = new HashMap<>();
        params.put("presence", "1");
        SlackMessageHandle<GenericSlackReply> handle = postGenericSlackCommand(params, LIST_USERS);
        GenericSlackReply replyEv = handle.getReply();
        JSONArray membersjson = (JSONArray) replyEv.getPlainAnswer().get("members");

        Map<String, SlackUser> members = new HashMap<>();
        if (membersjson != null) {
            Type listType = new TypeToken<List<SlackUser>>() {}.getType();
            List<SlackUser> users = gson.fromJson(membersjson.toJSONString(), listType);
            members = users.stream().collect(Collectors.toMap(SlackUser::getId, Function.identity()));
        }

        //blindly replace cache
        users = members;
    }

    private void postSlackCommand(Map<String, String> params, String command, SlackMessageHandleImpl handle) {
        HttpClient client = getHttpClient();
        HttpPost request = new HttpPost(SLACK_API_HTTPS_ROOT + command);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        for (Map.Entry<String, String> arg : params.entrySet())
        {
            nameValuePairList.add(new BasicNameValuePair(arg.getKey(), arg.getValue()));
        }
        try
        {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
            HttpResponse response = client.execute(request);
            String jsonResponse = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            LOGGER.debug("PostMessage return: " + jsonResponse);
            ImmutableSlackParsedReply reply = gson.fromJson(jsonResponse, ImmutableSlackParsedReply.class);
            handle.setReply(reply);
        }
        catch (Exception e)
        {
            // TODO : improve exception handling
            e.printStackTrace();
        }
    }

    private void postSlackCommandWithFile(Map<String, String> params, byte [] fileContent, String fileName, String command, SlackMessageHandleImpl handle) {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(SLACK_API_SCHEME).setHost(SLACK_API_HOST).setPath(SLACK_API_PATH+"/"+command);
        for (Map.Entry<String, String> arg : params.entrySet())
        {
            uriBuilder.setParameter(arg.getKey(),arg.getValue());
        }
        HttpPost request = new HttpPost(uriBuilder.toString());
        HttpClient client = getHttpClient();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        try
        {
            builder.addBinaryBody("file",fileContent, ContentType.DEFAULT_BINARY,fileName);
            request.setEntity(builder.build());
            HttpResponse response = client.execute(request);
            String jsonResponse = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            LOGGER.debug("PostMessage return: " + jsonResponse);
            ImmutableSlackParsedReply reply = gson.fromJson(jsonResponse, ImmutableSlackParsedReply.class);
            handle.setReply(reply);
        }
        catch (Exception e)
        {
            // TODO : improve exception handling
            e.printStackTrace();
        }
    }

    @Override
    public SlackMessageHandle<GenericSlackReply> postGenericSlackCommand(Map<String, String> params, String command) {
        HttpClient client = getHttpClient();
        HttpPost request = new HttpPost(SLACK_API_HTTPS_ROOT + command);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        for (Map.Entry<String, String> arg : params.entrySet())
        {
            if (!"token".equals(arg.getKey())) {
                nameValuePairList.add(new BasicNameValuePair(arg.getKey(), arg.getValue()));
            }
        }
        nameValuePairList.add(new BasicNameValuePair("token", authToken));
        try
        {
            SlackMessageHandleImpl<GenericSlackReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
            request.setEntity(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
            HttpResponse response = client.execute(request);
            String jsonResponse = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            LOGGER.debug("PostMessage return: " + jsonResponse);
            ImmutableGenericSlackReply reply = ImmutableGenericSlackReply.builder()
                    .plainAnswer(parseObject(jsonResponse))
                    .build();
            handle.setReply(reply);
            return handle;
        }
        catch (Exception e)
        {
            // TODO : improve exception handling
            e.printStackTrace();
        }
        return null;
    }

    private HttpClient getHttpClient() {
        HttpClient client;
        if (proxyHost != null)
        {
            client = HttpClientBuilder.create().setRoutePlanner(new DefaultProxyRoutePlanner(proxyHost)).build();
        }
        else
        {
            client = HttpClientBuilder.create().build();
        }
        return client;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendMessageOverWebSocket(SlackChannel channel, String message) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        try
        {
            JSONObject messageJSON = new JSONObject();
            messageJSON.put("type", "message");
            messageJSON.put("channel", channel.getId());
            messageJSON.put("text", message);

            websocketSession.getBasicRemote().sendText(messageJSON.toJSONString());
        }
        catch (Exception e)
        {
            // TODO : improve exception handling
            e.printStackTrace();
        }
        return handle;
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> sendTyping(SlackChannel channel) {
        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        try
        {
            JSONObject messageJSON = new JSONObject();
            messageJSON.put("type", "typing");
            messageJSON.put("channel", channel.getId());
            websocketSession.getBasicRemote().sendText(messageJSON.toJSONString());
        }
        catch (Exception e)
        {
            // TODO : improve exception handling
            e.printStackTrace();
        }
        return handle;
    }

    @Override
    public PresenceEnum getPresence(SlackUser persona) {
        HttpClient client = getHttpClient();
        HttpPost request = new HttpPost("https://slack.com/api/users.getPresence");
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("token", authToken));
        nameValuePairList.add(new BasicNameValuePair("user", persona.getId()));
        try
        {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
            HttpResponse response = client.execute(request);
            String jsonResponse = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            LOGGER.debug("PostMessage return: " + jsonResponse);

            ImmutableSlackParsedReply reply = gson.fromJson(jsonResponse, ImmutableSlackParsedReply.class);
            if (!reply.isOk() || !reply.getPresence().isPresent()) {
                return PresenceEnum.UNKNOWN;
            }
            if ("active".equals(reply.getPresence().get())) {
                return PresenceEnum.ACTIVE;
            }
            if ("away".equals(reply.getPresence().get())) {
                return PresenceEnum.AWAY;
            }
        }
        catch (Exception e)
        {
            // TODO : improve exception handling
            e.printStackTrace();
        }
        return PresenceEnum.UNKNOWN;
    }

    public void setPresence(PresenceEnum presenceEnum) {
        if(presenceEnum == PresenceEnum.UNKNOWN || presenceEnum == PresenceEnum.ACTIVE) {
            throw new IllegalArgumentException("PresenceEnum must be either AWAY or AUTO");
        }
        HttpClient client = getHttpClient();
        HttpPost request = new HttpPost(SLACK_API_HTTPS_ROOT + SET_PERSONA_ACTIVE);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("token", authToken));
        nameValuePairList.add(new BasicNameValuePair("presenceEnum", presenceEnum.toString().toLowerCase()));
        try {
            request.setEntity(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));
            HttpResponse response = client.execute(request);
            String JSONResponse = CharStreams.toString(new InputStreamReader(response.getEntity().getContent()));
            LOGGER.debug("JSON Response=" + JSONResponse);
        }catch(IOException e) {
            e.printStackTrace();
        }

    }

    private long getNextMessageId() {
        return messageId.getAndIncrement();
    }

    @Override
    public void onMessage(String message) {
        LOGGER.debug("receiving from websocket " + message);
        if (message.contains("{\"type\":\"pong\",\"reply_to\"")) {
            int rightBracketIdx = message.indexOf('}');
            String toParse = message.substring(26, rightBracketIdx);
            lastPingAck = Integer.parseInt(toParse);
            LOGGER.debug("pong received " + lastPingAck);
        }
        else {
            SlackEvent slackEvent;
            try {
                slackEvent = gson.fromJson(message, SlackEvent.class);
            } catch (Exception e) {
                LOGGER.error("Encountered error while parsing message json");
                e.printStackTrace();
                return;
            }

            eventBus.post(postProcessEvent(slackEvent));
        }
    }

    private SlackEvent postProcessEvent(SlackEvent slackEvent) {
        // TODO: There has got to be a better way to get the session into these immutable objects.
        if (slackEvent instanceof SlackChannelCreated) {
            SlackChannelCreated channelCreatedEvent = (SlackChannelCreated) slackEvent;
            channels.put(channelCreatedEvent.getChannel().getId(), channelCreatedEvent.getChannel());
            return ImmutableSlackChannelCreated.copyOf(channelCreatedEvent).withSlackSession(this);
        }
        if (slackEvent instanceof SlackGroupJoined) {
            SlackGroupJoined groupJoinedEvent = (SlackGroupJoined) slackEvent;
            channels.put(groupJoinedEvent.getChannel().getId(), groupJoinedEvent.getChannel());
            return ImmutableSlackGroupJoined.copyOf(groupJoinedEvent).withSlackSession(this);
        }
        if (slackEvent instanceof UserChange) {
            UserChange userChange = (UserChange) slackEvent;
            users.put(userChange.getUser().getId(), userChange.getUser());
            return ImmutableUserChange.copyOf(userChange).withSlackSession(this);
        }
        if (slackEvent instanceof SlackMessagePosted) {
            return ImmutableSlackMessagePosted.copyOf((SlackMessagePosted)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof SlackMessageUpdated) {
            return ImmutableSlackMessageUpdated.copyOf((SlackMessageUpdated)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof SlackMessageDeleted) {
            return ImmutableSlackMessageDeleted.copyOf((SlackMessageDeleted)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof SlackChannelArchived) {
            return ImmutableSlackChannelArchived.copyOf((SlackChannelArchived)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof SlackChannelDeleted) {
            return ImmutableSlackChannelDeleted.copyOf((SlackChannelDeleted)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof SlackChannelRenamed) {
            return ImmutableSlackChannelRenamed.copyOf((SlackChannelRenamed)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof SlackChannelUnarchived) {
            return ImmutableSlackChannelUnarchived.copyOf((SlackChannelUnarchived)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof SlackChannelJoined) {
            return ImmutableSlackChannelJoined.copyOf((SlackChannelJoined)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof SlackChannelLeft) {
            return ImmutableSlackChannelLeft.copyOf((SlackChannelLeft)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof ReactionAdded) {
            return ImmutableReactionAdded.copyOf((ReactionAdded)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof ReactionRemoved) {
            return ImmutableReactionRemoved.copyOf((ReactionRemoved)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof PresenceChange) {
            return ImmutablePresenceChange.copyOf((PresenceChange)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof PinAdded) {
            return ImmutablePinAdded.copyOf((PinAdded)slackEvent).withSlackSession(this);
        }
        if (slackEvent instanceof PinRemoved) {
            return ImmutablePinRemoved.copyOf((PinRemoved)slackEvent).withSlackSession(this);
        }

        return slackEvent;
    }

    private JSONObject parseObject(String json) {
        JSONParser parser = new JSONParser();
        try
        {
            JSONObject object = (JSONObject) parser.parse(json);
            return object;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SlackMessageHandle<SlackParsedReply> inviteUser(String email, String firstName, boolean setActive) {

        SlackMessageHandleImpl<SlackParsedReply> handle = new SlackMessageHandleImpl<>(getNextMessageId());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("token", authToken);
        arguments.put("email", email);
        arguments.put("first_name", firstName);
        arguments.put("set_active", ""+setActive);
        postSlackCommand(arguments, INVITE_USER_COMMAND, handle);
        return handle;
    }

    public long getHeartbeat() {
        return TimeUnit.MILLISECONDS.toSeconds(heartbeat);
    }

    public void setHeartbeat(long heartbeat, TimeUnit unit) {
        this.heartbeat = unit.toMillis(heartbeat);
    }
}
