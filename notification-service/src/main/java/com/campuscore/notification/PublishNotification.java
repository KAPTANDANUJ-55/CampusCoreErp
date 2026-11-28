package com.campuscore.notification;

import com.campuscore.notification.NotificationInbox.Audience;
import com.campuscore.notification.NotificationInbox.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

record PublishNotification(@NotNull UUID requestId, @NotBlank @Size(max = 120) String title,
                           @NotBlank @Size(max = 2000) String message, @NotNull Category category,
                           @NotNull Audience audience) {
}
