-- 다른 모듈(이 소유한 테이블들) 간의 FK 정의

-- message 모듈
ALTER TABLE message
    ADD CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES member(id),
    ADD CONSTRAINT fk_message_receiver FOREIGN KEY (receiver_id) REFERENCES member(id);

-- notification 모듈
ALTER TABLE notification
    ADD CONSTRAINT fk_notification_receiver FOREIGN KEY (receiver_id) REFERENCES member(id);

-- community 모듈
ALTER TABLE system_notice
    ADD CONSTRAINT fk_system_notice_member FOREIGN KEY (member_id) REFERENCES member(id);

ALTER TABLE post
    ADD CONSTRAINT fk_post_member FOREIGN KEY (member_id) REFERENCES member(id);

ALTER TABLE post_reaction
    ADD CONSTRAINT fk_post_reaction_member FOREIGN KEY (member_id) REFERENCES member(id);

ALTER TABLE comment
    ADD CONSTRAINT fk_comment_member FOREIGN KEY (member_id) REFERENCES member(id);

ALTER TABLE comment_reaction
    ADD CONSTRAINT fk_comment_reaction_member FOREIGN KEY (member_id) REFERENCES member(id);

ALTER TABLE post_report
    ADD CONSTRAINT fk_post_report_member FOREIGN KEY (member_id) REFERENCES member(id),
    ADD CONSTRAINT fk_post_report_processed_by FOREIGN KEY (processed_by) REFERENCES member(id);

ALTER TABLE comment_report
    ADD CONSTRAINT fk_comment_report_member FOREIGN KEY (member_id) REFERENCES member(id),
    ADD CONSTRAINT fk_comment_report_processed_by FOREIGN KEY (processed_by) REFERENCES member(id);

ALTER TABLE board_favorite
    ADD CONSTRAINT fk_board_favorite_member FOREIGN KEY (member_id) REFERENCES member(id);

ALTER TABLE board_manager
    ADD CONSTRAINT fk_board_manager_member FOREIGN KEY (member_id) REFERENCES member(id);

ALTER TABLE board_member_suspension
    ADD CONSTRAINT fk_board_member_suspension_member FOREIGN KEY (member_id) REFERENCES member(id),
    ADD CONSTRAINT fk_board_member_suspension_lifted_by FOREIGN KEY (lifted_by) REFERENCES member(id),
    ADD CONSTRAINT fk_board_member_suspension_created_by FOREIGN KEY (created_by) REFERENCES member(id);

-- admin 컴포넌트
ALTER TABLE management_log
    ADD CONSTRAINT fk_management_log_actor FOREIGN KEY (actor_id) REFERENCES member(id);
