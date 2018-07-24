package com.icumister.notification;

public enum NotificationFilter {
    ALL(0),
    UNKNOWN(1),
    KNOWN(2),
    LONG_STANDING(3)
    ;

    private final int value;

    NotificationFilter(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
