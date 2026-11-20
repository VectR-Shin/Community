SET @now = CURRENT_TIMESTAMP(6);

INSERT INTO board_category (name, created_at, updated_at) VALUES ('기타', @now, @now);