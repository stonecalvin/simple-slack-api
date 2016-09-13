package com.ullink.slack.simpleslackapi;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Gson.TypeAdapters
@Value.Immutable
public interface SlackAttachment {
    String getText();
    String getFooter();
    String getColor();

    Optional<String> getTitle();
    Optional<String> getTitleLink();
    Optional<String> getFallback();
    Optional<String> getPretext();

    @SerializedName("author_name") String getAuthorName();
    @SerializedName("author_link") String getAuthorLink();
    @SerializedName("author_icon") String getAuthorIcon();

    @SerializedName("callback_id") Optional<String> getCallbackId();
    @SerializedName("thumb_url") Optional<String> getThumbUrl();
    @SerializedName("footer_icon") Optional<String> getFooterIcon();
    @SerializedName("image_url") Optional<String> getImageUrl();

    Map<String, String> getMiscRootFields();
    List<SlackField> getFields();
    List<SlackAction> getActions();
    @SerializedName("markdown_in") List<String> getMarkdownIn();
}
