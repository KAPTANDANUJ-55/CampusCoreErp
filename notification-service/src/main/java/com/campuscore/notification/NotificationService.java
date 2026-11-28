package com.campuscore.notification;

import com.campuscore.notification.NotificationInbox.Audience;
import com.campuscore.notification.NotificationInbox.Category;
import com.campuscore.notification.NotificationInbox.Item;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Types;
import java.time.Instant;
import java.util.List;

@Service
class NotificationService {

    private static final int PAGE_SIZE = 20;
    private static final String VISIBLE = """
            (n.sender_id = :userId OR
                ((n.department_key IS NULL OR n.department_key = :departmentKey)
                 AND (n.audience = 'EVERYONE'
                      OR (n.audience = 'STUDENTS' AND :role = 'ROLE_STUDENT')
                      OR (n.audience = 'STAFF' AND :role = 'ROLE_STAFF'))))
            """;
    private static final String INBOX_FROM = """
            FROM campus_notification n
            LEFT JOIN notification_read r ON r.notification_id = n.id AND r.user_id = :userId
            WHERE
            """;
    private static final String SELECT_RECEIPT = """
            INSERT INTO notification_read (user_id, notification_id)
            SELECT :userId, n.id FROM campus_notification n
            WHERE
            """;
    private static final String ON_READ_CONFLICT = " ON DUPLICATE KEY UPDATE notification_id = notification_read.notification_id";
    private static final String UNREAD = "r.notification_id IS NULL AND n.sender_id <> :userId";

    private final JdbcClient jdbc;

    NotificationService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public NotificationInbox inbox(NotificationUser user, boolean unreadOnly, Long before) {
        InboxCounts counts = forUser("""
                SELECT COUNT(CASE WHEN %s THEN 1 END) AS unread_count,
                       COALESCE(MAX(n.id), 0) AS latest_id
                """.formatted(UNREAD) + INBOX_FROM + VISIBLE, user)
                .query((rs, row) -> new InboxCounts(rs.getLong("unread_count"), rs.getLong("latest_id")))
                .single();

        String sql = """
                SELECT n.id, n.title, n.message, n.category, n.audience, n.department, n.created_at_ms,
                       (r.notification_id IS NOT NULL OR n.sender_id = :userId) AS is_read
                """ + INBOX_FROM + VISIBLE
                + (unreadOnly ? " AND " + UNREAD : "")
                + (before == null ? "" : " AND n.id < :before")
                + " ORDER BY n.id DESC LIMIT :limit";

        var query = forUser(sql, user).param("limit", PAGE_SIZE + 1);
        if (before != null) query.param("before", before);
        List<Item> items = query.query((rs, row) -> new Item(
                rs.getLong("id"), rs.getString("title"), rs.getString("message"),
                Category.valueOf(rs.getString("category")), Audience.valueOf(rs.getString("audience")),
                rs.getString("department"), Instant.ofEpochMilli(rs.getLong("created_at_ms")),
                rs.getBoolean("is_read"))).list();

        if (items.size() > PAGE_SIZE) {
            List<Item> page = List.copyOf(items.subList(0, PAGE_SIZE));
            return new NotificationInbox(page, counts.unreadCount(), counts.latestId(), page.getLast().id());
        }
        return new NotificationInbox(items, counts.unreadCount(), counts.latestId(), null);
    }

    public void publish(NotificationUser user, PublishNotification request) {
        String department = switch (user.role()) {
            case ROLE_PRINCIPAL -> null;
            case ROLE_HOD -> {
                if (user.department() == null) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account needs an assigned department.");
                }
                yield user.department();
            }
            default -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your role cannot publish notifications.");
        };

        String title = request.title().strip();
        String message = request.message().strip();
        String requestId = request.requestId().toString();
        try {
            jdbc.sql("""
                    INSERT INTO campus_notification
                        (sender_id, request_id, title, message, category, audience, department, department_key, created_at_ms)
                    VALUES (:senderId, :requestId, :title, :message, :category, :audience, :department, :departmentKey, :now)
                    """)
                    .param("senderId", user.idKey())
                    .param("requestId", requestId)
                    .param("title", title)
                    .param("message", message)
                    .param("category", request.category().name())
                    .param("audience", request.audience().name())
                    .param("department", department, Types.VARCHAR)
                    .param("departmentKey", department == null ? null : user.departmentKey(), Types.VARBINARY)
                    .param("now", Instant.now().toEpochMilli())
                    .update();
        } catch (DuplicateKeyException ex) {
            var previous = jdbc.sql("""
                    SELECT title, message, category, audience, department FROM campus_notification
                    WHERE sender_id = :senderId AND request_id = :requestId
                    """)
                    .param("senderId", user.idKey())
                    .param("requestId", requestId)
                    .query((rs, row) -> new PublishedContent(rs.getString("title"), rs.getString("message"),
                            rs.getString("category"), rs.getString("audience"), rs.getString("department")))
                    .optional();
            var submitted = new PublishedContent(title, message, request.category().name(), request.audience().name(),
                    department);
            if (previous.isEmpty()) throw ex;
            if (!previous.get().equals(submitted)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This request ID was used for another notification.");
            }
        }
    }

    public void markRead(NotificationUser user, long id) {
        forUser(SELECT_RECEIPT + VISIBLE + " AND n.sender_id <> :userId AND n.id = :id" + ON_READ_CONFLICT, user)
                .param("id", id)
                .update();
    }

    public void markAllRead(NotificationUser user, long throughId) {
        forUser(SELECT_RECEIPT + VISIBLE + " AND n.sender_id <> :userId AND n.id <= :id ORDER BY n.id" + ON_READ_CONFLICT, user)
                .param("id", throughId)
                .update();
    }

    private JdbcClient.StatementSpec forUser(String sql, NotificationUser user) {
        return jdbc.sql(sql)
                .param("userId", user.idKey())
                .param("departmentKey", user.departmentKey(), Types.VARBINARY)
                .param("role", user.role().name());
    }

    private record InboxCounts(long unreadCount, long latestId) {
    }

    private record PublishedContent(String title, String message, String category, String audience, String departmentKey) {
    }
}
