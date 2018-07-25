package com.icumister.notification;

import com.icumister.AppState;

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

    public static boolean matchedNotification(Notification notification) {
        if(AppState.notificationFilter == ALL) {
            return true;
        }

        switch(notification.getNotifType()) {
            case KNOWN:
                return AppState.notificationFilter == KNOWN;
            default:
                int LONG_STANDING_MIN_VALUE = 5;
                return AppState.notificationFilter == UNKNOWN
                        || (AppState.notificationFilter == LONG_STANDING
                                && notification.getTime_elapsed() >= LONG_STANDING_MIN_VALUE);

        }
    }
}
