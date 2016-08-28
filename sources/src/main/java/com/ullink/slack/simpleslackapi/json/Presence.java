package com.ullink.slack.simpleslackapi.json;

import com.google.gson.annotations.SerializedName;

public enum Presence {
    UNKNOWN,
    @SerializedName("active") ACTIVE,
    @SerializedName("away") AWAY,
    @SerializedName("auto") AUTO
}