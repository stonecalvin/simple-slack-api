package com.ullink.slack.simpleslackapi.replies;

import com.google.gson.annotations.SerializedName;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Optional;

@Value.Immutable
@Gson.TypeAdapters
public abstract class SlackParsedReply implements SlackReply {
    public abstract boolean isOk();

    public abstract Optional<String> getId();
    public abstract Optional<String> getPresence();

    @SerializedName("channel") public abstract Optional<SlackChannel> getSlackChannel();
    @SerializedName("emoji") public abstract Optional<Map<String, String>> getEmojis();
    @SerializedName("error") public abstract Optional<String> getErrorMessage();
    @SerializedName("reply_to") public abstract Optional<Long> getReplyTo();
    @SerializedName("cache_ts") public abstract Optional<String> getCacheTimestamp();
    @SerializedName("ts") public abstract Optional<String> getTimestamp();
    @SerializedName("is_mpim") public abstract Optional<Boolean> isMpim();
    @SerializedName("is_im") public abstract Optional<Boolean> isIm();
    @SerializedName("is_group") public abstract Optional<Boolean> isGroup();

    @Value.Lazy
    public boolean isActive() {
        return this.getPresence().isPresent() && "active".equals(this.getPresence().get());
    }

}
