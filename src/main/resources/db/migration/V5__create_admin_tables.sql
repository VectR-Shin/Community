CREATE TABLE admin_action_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    target_id BIGINT NULL,
    target_type VARCHAR(20) NOT NULL,
    action_type VARCHAR(30) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_admin_action_log PRIMARY KEY (id)
);
