package com.campuscore.notification;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

record NotificationUser(String id, Role role, String department) {

    static final String REQUEST_ATTRIBUTE = "notificationUser";

    enum Role {
        ROLE_STUDENT, ROLE_STAFF, ROLE_HOD, ROLE_PRINCIPAL
    }

    byte[] idKey() {
        return id.getBytes(StandardCharsets.UTF_8);
    }

    byte[] departmentKey() {
        return department == null ? null : department.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8);
    }
}
