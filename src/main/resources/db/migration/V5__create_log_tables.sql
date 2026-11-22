CREATE TABLE management_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    actor_id BIGINT NOT NULL,
    actor_role VARCHAR(20) NOT NULL,
    target_id BIGINT NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    action_type VARCHAR(30) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_management_log PRIMARY KEY (id),

    CONSTRAINT chk_management_log_actor_role CHECK (actor_role IN ('ADMIN', 'MANAGER'))
);
