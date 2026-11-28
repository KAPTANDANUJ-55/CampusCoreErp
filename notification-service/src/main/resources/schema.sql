CREATE TABLE IF NOT EXISTS campus_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id VARBINARY(1020) NOT NULL,
    request_id CHAR(36) NOT NULL,
    title VARCHAR(120) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    category VARCHAR(24) NOT NULL,
    audience VARCHAR(24) NOT NULL,
    department VARCHAR(255),
    department_key VARBINARY(2048),
    created_at_ms BIGINT NOT NULL,
    CONSTRAINT notification_request_unique UNIQUE (sender_id, request_id)
);

CREATE TABLE IF NOT EXISTS notification_read (
    user_id VARBINARY(1020) NOT NULL,
    notification_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, notification_id),
    FOREIGN KEY (notification_id) REFERENCES campus_notification(id)
);
