package com.icumister.notification;

import android.os.Bundle;

public class Notification {


    private final String msg;
    private NotifType notifType;
    private final String url;
    private int time_elapsed;

    public Notification(Bundle bundle) {
        this.msg = bundle.getString("msg");
        this.notifType = NotifType.valueOf(bundle.getString("type"));
        this.url = bundle.getString("url");
        this.time_elapsed = Integer.valueOf(bundle.getString("time_elapsed"));
    }

    public String getMsg() {
        return msg;
    }

    public NotifType getNotifType() {
        return notifType;
    }

    public String getUrl() {
        return url;
    }

    public int getTime_elapsed() {
        return time_elapsed;
    }

    public enum NotifType {
        UNKNOWN("UNKNOWN"),
        KNOWN("KNOWN")
        ;

        private final String text;

        NotifType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
