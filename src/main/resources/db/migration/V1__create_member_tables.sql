CREATE TABLE member (
    id BIGINT NOT NULL AUTO_INCREMENT,
    keycloak_sub VARCHAR(255) NULL,
    email VARCHAR(255) NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,

    CONSTRAINT pk_member PRIMARY KEY (id),

    CONSTRAINT uq_member_keycloak_sub UNIQUE (keycloak_sub),
    CONSTRAINT uq_member_email UNIQUE (email),

    CONSTRAINT chk_member_role CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE withdrawal_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email_hash VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_sub_hash VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    blocked_until DATETIME(6) NOT NULL,

    CONSTRAINT pk_withdrawal_record PRIMARY KEY (id),

    CONSTRAINT chk_withdrawal_record_provider CHECK (provider IN ('LOCAL', 'GOOGLE', 'KAKAO', 'NAVER'))
);

CREATE TABLE profile (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    nickname VARCHAR(20) NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_profile PRIMARY KEY (id),

    CONSTRAINT uq_profile_member_id UNIQUE (member_id),
    CONSTRAINT uq_profile_nickname UNIQUE (nickname),

    CONSTRAINT fk_profile_member FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE member_suspension (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    reason VARCHAR(50) NOT NULL,
    ends_at DATETIME(6) NULL,
    lifted_at DATETIME(6) NULL,
    lifted_by BIGINT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_member_suspension PRIMARY KEY (id),

    CONSTRAINT fk_member_suspension_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_member_suspension_lifted_by FOREIGN KEY (lifted_by) REFERENCES member(id),
    CONSTRAINT fk_member_suspension_created_by FOREIGN KEY (created_by) REFERENCES member(id),

    CONSTRAINT chk_member_suspension_lifted_info CHECK (
        (lifted_at IS NULL AND lifted_by IS NULL)
        OR
        (lifted_at IS NOT NULL AND lifted_by IS NOT NULL)
    )
);

CREATE TABLE block (
    id BIGINT NOT NULL AUTO_INCREMENT,
    blocker_id BIGINT NOT NULL,
    blocked_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_block PRIMARY KEY (id),

    CONSTRAINT uq_block_blocker_blocked UNIQUE (blocker_id, blocked_id),

    CONSTRAINT fk_block_blocker FOREIGN KEY (blocker_id) REFERENCES member(id),
    CONSTRAINT fk_block_blocked FOREIGN KEY (blocked_id) REFERENCES member(id),

    CONSTRAINT chk_block_no_self_block CHECK(blocker_id <> blocked_id)
);
