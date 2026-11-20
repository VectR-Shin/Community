CREATE TABLE notification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receiver_id BIGINT NOT NULL,
    notification_type VARCHAR(30) NOT NULL,
    content VARCHAR(200) NOT NULL,
    target_type VARCHAR(20) NULL,
    target_id BIGINT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_notification PRIMARY KEY (id),

    CONSTRAINT chk_notification_target_info CHECK (
        (target_type IS NULL AND target_id IS NULL)
        OR
        (target_type IS NOT NULL AND target_id IS NOT NULL)
    )
);

CREATE TABLE system_notification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content VARCHAR(200) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_system_notification PRIMARY KEY (id)
);