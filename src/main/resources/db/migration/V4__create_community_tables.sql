CREATE TABLE board_category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_board_category PRIMARY KEY (id),

    CONSTRAINT uq_board_category_name UNIQUE KEY (name)
);

CREATE TABLE board (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(20) NOT NULL,
    description VARCHAR(100) NULL,
    board_status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,

    CONSTRAINT pk_board PRIMARY KEY (id),

    CONSTRAINT uq_board_name UNIQUE KEY (name),

    CONSTRAINT fk_board_category FOREIGN KEY (category_id) REFERENCES board_category(id),

    CONSTRAINT chk_board_status CHECK (board_status IN ('NORMAL', 'READ_ONLY'))
);

CREATE TABLE post (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    board_id BIGINT NOT NULL,
    title VARCHAR(50) NOT NULL,
    content VARCHAR(5000) NOT NULL,
    comment_count BIGINT NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    like_count BIGINT NOT NULL DEFAULT 0,
    dislike_count BIGINT NOT NULL DEFAULT 0,
    is_popular BOOLEAN NOT NULL DEFAULT FALSE,
    post_type VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,

    CONSTRAINT pk_post PRIMARY KEY (id),

    CONSTRAINT fk_post_board FOREIGN KEY (board_id) REFERENCES board(id),

    CONSTRAINT chk_post_post_type CHECK (post_type IN ('POST', 'NOTICE'))
);

CREATE TABLE post_view (
    id BIGINT NOT NULL AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    uuid VARCHAR(50) NOT NULL,
    last_viewed_at DATETIME(6) NOT NULL,

    CONSTRAINT pk_post_view PRIMARY KEY (id),

    UNIQUE KEY uq_post_view_post_uuid (post_id, uuid),

    CONSTRAINT fk_post_view_post FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE TABLE post_reaction (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    reaction_type VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_post_reaction PRIMARY KEY (id),

    UNIQUE KEY uq_post_reaction_member_post (member_id, post_id),

    CONSTRAINT fk_post_reaction_post FOREIGN KEY (post_id) REFERENCES post(id),

    CONSTRAINT chk_post_reaction_reaction_type CHECK (reaction_type IN ('LIKE', 'DISLIKE'))
);

CREATE TABLE comment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    parent_id BIGINT NULL,
    content VARCHAR(500) NOT NULL,
    like_count BIGINT NOT NULL DEFAULT 0,
    dislike_count BIGINT NOT NULL DEFAULT 0,
    is_popular BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,

    CONSTRAINT pk_comment PRIMARY KEY (id),

    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post(id),

    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id)
);

CREATE TABLE comment_reaction (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    reaction_type VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_comment_reaction PRIMARY KEY (id),

    CONSTRAINT uq_comment_reaction_member_comment UNIQUE KEY (member_id, comment_id),

    CONSTRAINT fk_comment_reaction_comment FOREIGN KEY (comment_id) REFERENCES comment(id),

    CONSTRAINT chk_comment_reaction_reaction_type CHECK (reaction_type IN ('LIKE', 'DISLIKE'))
);

CREATE TABLE post_report (
    id BIGINT NOT NULL AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    content VARCHAR(100) NULL,
    reason VARCHAR(50) NOT NULL,
    process_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    processed_at DATETIME(6) NULL,
    processed_by BIGINT NULL,

    CONSTRAINT pk_post_report PRIMARY KEY (id),

    CONSTRAINT uq_post_report_post_member UNIQUE KEY (post_id, member_id),

    CONSTRAINT fk_post_report_post FOREIGN KEY (post_id) REFERENCES post(id),

    CONSTRAINT chk_post_report_reason CHECK (reason IN ('ABUSE', 'SPAM', 'INAPPROPRIATE_CONTENT', 'OTHER')),
    CONSTRAINT chk_post_process_status CHECK (process_status IN ('PENDING', 'PROCESSED', 'REJECTED')),
    CONSTRAINT chk_post_report_processed_info CHECK (
        (processed_at IS NULL AND processed_by IS NULL)
        OR
        (processed_at IS NOT NULL AND processed_by IS NOT NULL)
    )
);

CREATE TABLE comment_report (
    id BIGINT NOT NULL AUTO_INCREMENT,
    comment_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    content VARCHAR(100) NULL,
    reason VARCHAR(50) NOT NULL,
    process_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    processed_at DATETIME(6) NULL,
    processed_by BIGINT NULL,

    CONSTRAINT pk_comment_report PRIMARY KEY (id),

    CONSTRAINT uq_comment_report_comment_member UNIQUE KEY (comment_id, member_id),

    CONSTRAINT fk_comment_report_comment FOREIGN KEY (comment_id) REFERENCES comment(id),

    CONSTRAINT chk_comment_report_reason CHECK (reason IN ('ABUSE', 'SPAM', 'INAPPROPRIATE_CONTENT', 'OTHER')),
    CONSTRAINT chk_comment_process_status CHECK (process_status IN ('PENDING', 'PROCESSED', 'REJECTED')),
    CONSTRAINT chk_comment_report_processed_info CHECK (
        (processed_at IS NULL AND processed_by IS NULL)
        OR
        (processed_at IS NOT NULL AND processed_by IS NOT NULL)
    )
);

CREATE TABLE board_favorite (
    id BIGINT NOT NULL AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_board_favorite PRIMARY KEY (id),

    CONSTRAINT uq_board_favorite_board_member UNIQUE KEY (board_id, member_id),

    CONSTRAINT fk_board_favorite_board FOREIGN KEY (board_id) REFERENCES board(id)
);

CREATE TABLE board_manager (
    id BIGINT NOT NULL AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_board_manager PRIMARY KEY (id),

    CONSTRAINT uq_board_manager_board_member UNIQUE KEY (board_id, member_id),

    CONSTRAINT fk_board_manager_board FOREIGN KEY (board_id) REFERENCES board(id),

    CONSTRAINT chk_board_manager_role CHECK (role IN ('MANAGER', 'SUB_MANAGER'))
);

CREATE TABLE board_member_suspension (
    id BIGINT NOT NULL AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    reason VARCHAR(50) NOT NULL,
    ends_at DATETIME(6) NULL,
    lifted_by BIGINT NULL,
    lifted_at DATETIME(6) NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_board_member_suspension PRIMARY KEY (id),

    CONSTRAINT fk_board_member_suspension_board FOREIGN KEY (board_id) REFERENCES board(id),

    CONSTRAINT chk_board_member_suspension_reason CHECK (reason IN ('ABUSE', 'SPAM', 'INAPPROPRIATE_CONTENT', 'OTHER')),
    CONSTRAINT chk_board_member_suspension_lifted_info CHECK (
        (lifted_at IS NULL AND lifted_by IS NULL)
        OR
        (lifted_at IS NOT NULL AND lifted_by IS NOT NULL)
    )
);
