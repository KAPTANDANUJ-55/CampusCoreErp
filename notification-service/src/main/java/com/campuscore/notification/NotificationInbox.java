package com.campuscore.notification;

import java.time.Instant;
import java.util.List;

record NotificationInbox(List<Item> items, long unreadCount, long latestId, Long nextBefore) {

    enum Category {
        ANNOUNCEMENT, ACADEMIC, REMINDER
    }

    enum Audience {
        EVERYONE, STUDENTS, STAFF
    }

    record Item(long id, String title, String message, Category category, Audience audience,
                String department, Instant createdAt, boolean read) {
    }
}
