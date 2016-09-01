package com.ullink.slack.simpleslackapi.json;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface MyFile {
    String id();
    String name();
    String title();
    String mimetype();
    String filetype();
    String url();
    String urlDownload();
    String urlPrivate();
    String urlPrivateDownload();
    String thumb64();
    String thumb80();
    String thumb160();
    String thumb360();
    String thumb480();
    String thumb720();
    Long imageExifRotation();
    Long originalW();
    Long originalH();
    String permalink();
    String permalinkPublic();
    String comment();
}
