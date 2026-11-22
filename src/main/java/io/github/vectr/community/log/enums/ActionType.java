package io.github.vectr.community.log.enums;

public enum ActionType {
    SUSPEND_MEMBER,// 사용자 전체 이용 정지
    LIFT_MEMBER,// 사용자 전체 이용 정지 해제
    SUSPEND_BOARD_MEMBER,// 사용자 게시판 이용 정지
    LIFT_BOARD_MEMBER,// 사용자 게시판 이용 정지 해제
    MEMBER_ROLE_CHANGE,// 사용자 권한 변경
    CREATE_BOARD,// 게시판 생성
    UPDATE_BOARD,// 게시판 수정
    DELETE_BOARD,// 게시판 삭제
    RESTORE_BOARD,// 게시판 복구
    DELETE_POST,// 게시글 삭제
    RESTORE_POST,// 게시글 복구
    CREATE_SYSTEM_NOTICE,// 전체 공지글 생성
    UPDATE_SYSTEM_NOTICE,// 전체 공지글 수정
    DELETE_SYSTEM_NOTICE,// 전체 공지글 삭제
    RESTORE_SYSTEM_NOTICE,// 전체 공지굴 복구
    CREATE_NOTICE,// 공지글 생성
    UPDATE_NOTICE,// 공지글 수정
    DELETE_NOTICE,// 공지글 삭제
    RESTORE_NOTICE,// 공지글 복구
    DELETE_COMMENT,// 댓글 삭제
    RESTORE_COMMENT,// 댓글 복구
    PROCESS_POST_REPORT,// 게시글 신고 처리
    PROCESS_COMMENT_REPORT,// 댓글 신고 처리
    BROADCAST// 시스템 알림 브로드캐스트
}
