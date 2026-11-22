SET @now = CURRENT_TIMESTAMP(6);

INSERT INTO board_category (name, created_at, updated_at)
VALUES
    ('일반', @now, @now),
    ('IT·기술', @now, @now),
    ('게임', @now, @now),
    ('취미·여가', @now, @now),
    ('스포츠', @now, @now),
    ('엔터테인먼트', @now, @now),
    ('교육·학습', @now, @now),
    ('경제·금융', @now, @now),
    ('생활·정보', @now, @now),
    ('질문·답변', @now, @now);