package com.ullink.slack.simpleslackapi.json;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public interface MyAttachment {
    String text();
    String footer();
    String color();

    Optional<String> title();
    Optional<String> titleLink();
    Optional<String> fallback();
    Optional<String> pretext();

    @SerializedName("author_name") String authorName();
    @SerializedName("author_link") String authorLink();
    @SerializedName("author_icon") String authorIcon();

    @SerializedName("callback_id") Optional<String> callbackId();
    @SerializedName("thumb_url") Optional<String> thumbUrl();
    @SerializedName("footer_icon") Optional<String> footerIcon();
    @SerializedName("image_url") Optional<String> imageUrl();

    Map<String, String> miscRootFields();
    List<MyField> fields();
    List<MyAction> actions();
    List<String> markdown_in();
}
