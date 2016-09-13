package com.ullink.slack.simpleslackapi;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface SlackFile {
    String getId();
    String getName();
    String getTitle();
    String getMimetype();
    String getFiletype();
    String getUrl();
    String getUrlDownload();
    String getUrlPrivate();
    String getUrlPrivateDownload();
    String getThumb64();
    String getThumb80();
    String getThumb160();
    String getThumb360();
    String getThumb480();
    String getThumb720();
    Long getImageExifRotation();
    Long getOriginalW();
    Long getOriginalH();
    String getPermalink();
    String getPermalinkPublic();
    String getComment();
}
