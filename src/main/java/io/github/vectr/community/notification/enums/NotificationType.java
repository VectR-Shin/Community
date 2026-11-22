package io.github.vectr.community.notification.enums;

public enum NotificationType {
    SYSTEM,// 시스템 안내
    MESSAGE_RECEIVED,// 메시지 도착
    SYSTEM_NOTICE,// 전체 공지사항 생성
    SYSTEM_NOTICE_DELETED,// 내 전체 공지글이 운영자에 의해 삭제
    SYSTEM_NOTICE_RESTORED,// 내 전체 공지글이 운영자에 의해 복구
    BOARD_MODIFIED,// 즐겨찾기한 게시판 변경, 운영자에 의해 내가 관리하는 게시판 변경
    BOARD_NOTICE,// 즐겨찾기한 게시판에 공지사항 생성
    BOARD_DELETED,// 즐겨찾기한 게시판 삭제, 운영자에 의해 내가 관리하는 게시판 삭제
    BOARD_RESTORED,// 즐겨찾기한 게시판 복구
    POST_DELETED,// 내 게시글이 강제 삭제 됨
    POST_RESTORED,// 내 게시글이 복구 됨
    NOTICE_DELETED,// 내 게시판 공지글이 강제 삭제
    NOTICE_RESTORED,// 내 게시판 공지글이 복구
    COMMENT_DELETED,// 내 댓글이 강제 삭제
    COMMENT_RESTORED,// 내 댓글이 복구 됨
    POPULAR_POST,// 내 게시글이 인기글로 고정 됨(작성 72시간 뒤)
    POPULAR_COMMENT,// 내 댓글이 인기댓글로 고정 됨(작성 12시간 뒤)
    ROLE_CHANGED,// 권한 변경
    SUSPENDED,// 전역 활동 제한 적용
    UNSUSPENDED,// 전역 활동 제한 만료
    SUSPENSION_LIFTED,// 전역 활동 제한이 운영자에 의해 해제
    BOARD_SUSPENDED,// 게시판 활동 제한 적용
    BOARD_UNSUSPENDED,// 게시판 활동 제한 만료
    BOARD_SUSPENSION_LIFTED// 게시판 활동 제한이 관리자에 의해 해제
}
