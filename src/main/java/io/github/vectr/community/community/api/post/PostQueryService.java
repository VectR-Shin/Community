package io.github.vectr.community.community.api.post;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.dto.post.PostSummaryResponseDTO;

public interface PostQueryService {
    /*
        memberId 를 식별자로 가지는 사용자가 작성한 게시글 목록 조회
        삭제된 게시판의 게시글은 조회 안 함
        공지글도 포함
        paging 기능
    */
    PageResponseDTO<PostSummaryResponseDTO> getUserPosts(long memberId, int page, int size);
}
