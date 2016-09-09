package com.ullink.slack.simpleslackapi.replies;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.json.Channel;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
@Gson.TypeAdapters
public abstract class MyParsedReply implements SlackReply {
    public abstract boolean ok();

    public abstract Optional<String> id();
    public abstract Optional<String> error();
    public abstract Optional<String> presence();
    public abstract Optional<Channel> channel();
    public abstract Optional<Map<String, String>> emoji();

    @SerializedName("reply_to") public abstract Optional<Long> replyTo();
    @SerializedName("cache_ts") public abstract Optional<String> cacheTimestamp();
    @SerializedName("ts") public abstract Optional<String> timestamp();
    @SerializedName("is_mpim") public abstract Optional<Boolean> mpim();
    @SerializedName("is_im") public abstract Optional<Boolean> im();
    @SerializedName("is_group") public abstract Optional<Boolean> group();

    @Value.Lazy
    public boolean active() {
        return this.presence().isPresent() && "active".equals(this.presence().get());
    }

}
