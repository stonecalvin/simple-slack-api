package com.ullink.slack.simpleslackapi;

import com.google.gson.annotations.SerializedName;

public enum PresenceEnum {
    UNKNOWN,
    @SerializedName("active") ACTIVE,
    @SerializedName("away") AWAY,
    @SerializedName("auto") AUTO
}