package com.campuscore.notification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController {

    private final NotificationService notifications;

    NotificationController(NotificationService notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    ResponseEntity<NotificationInbox> inbox(
            @RequestAttribute(NotificationUser.REQUEST_ATTRIBUTE) NotificationUser user,
            @RequestParam(defaultValue = "false") boolean unread,
            @RequestParam(required = false) @Positive Long before) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(notifications.inbox(user, unread, before));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void publish(@RequestAttribute(NotificationUser.REQUEST_ATTRIBUTE) NotificationUser user,
                 @Valid @RequestBody PublishNotification request) {
        notifications.publish(user, request);
    }

    @PostMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void markRead(@RequestAttribute(NotificationUser.REQUEST_ATTRIBUTE) NotificationUser user,
                  @PathVariable @Positive long id) {
        notifications.markRead(user, id);
    }

    @PostMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void markAllRead(@RequestAttribute(NotificationUser.REQUEST_ATTRIBUTE) NotificationUser user,
                     @Valid @RequestBody ReadAll request) {
        notifications.markAllRead(user, request.throughId());
    }

    record ReadAll(@Positive long throughId) {
    }
}
