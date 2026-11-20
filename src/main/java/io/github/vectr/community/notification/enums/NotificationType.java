package io.github.vectr.community.notification.enums;

public enum NotificationType {
    SYSTEM,// 시스템 안내
    MESSAGE_RECEIVED,// 메시지 도착
    BOARD_MODIFIED,// 즐겨찾기한 게시판 변경
    COMMENT,// 내 게시글에 댓글 달림
    NOTICE,// 즐겨찾기한 게시판의 공지 알림
    POPULAR_POST,// 내 게시글이 인기글이 됨
    POPULAR_COMMENT,// 내 댓글이 인기댓글이 됨
    BOARD_DELETED,// 즐겨찾기한 게시판 삭제
    POST_DELETED,// 내 게시글 강제 삭제
    POST_RESTORED,// 내 게시글 삭제 복구
    NOTICE_DELETED,// 내 공지글 강제 삭제
    COMMENT_DELETED,// 내 댓글 강제 삭제
    COMMENT_RESTORED,// 대 댓글 삭제 복구
    ROLE_CHANGE,// 내 권한이 변경됨
    SUSPENSION,// 전역 활동 제한
    SUSPENSION_LIFTED,// 전역 활동 제한 중도 해제
    BOARD_SUSPENSION,// 게시판 활동 제한
    BOARD_SUSPENSION_LIFTED// 게시판 활동 중도 해제
}
