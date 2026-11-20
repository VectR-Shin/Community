package io.github.vectr.community.community.service.post;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.api.post.PostQueryService;
import io.github.vectr.community.community.dto.post.PostSummaryResponseDTO;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.repository.post.PostRepository;
import io.github.vectr.community.member.api.user.UserQueryService;
import io.github.vectr.community.member.dto.user.AuthorInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService {
    private final PostRepository postRepository;

    private final UserQueryService userQueryService;

    /*
        memberId 를 식별자로 가지는 사용자가 작성한 게시글 목록 조회
        삭제된 게시판의 게시글은 조회 안 함
        공지글도 포함
        paging 기능
    */
    @Override
    public PageResponseDTO<PostSummaryResponseDTO> getUserPosts(long memberId, int page, int size) {
        // 1. 페이징
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")
                )
        );

        // 2. 게시글 조회
        Page<Post> posts = postRepository.findAllByMemberIdAndBoardNotDeleted(memberId, pageable);

        // 3. 작성자 정보 조회 (특정 사용자를 대상으로 하므로 단건 조회)
        AuthorInfoDTO authorInfoDTO = userQueryService.getAuthorInfo(memberId);

        // 4. DTO 변환
        Page<PostSummaryResponseDTO> content = posts.map(post ->
                new PostSummaryResponseDTO(
                        post.getId(),
                        post.getBoard().getId(),
                        post.getBoard().getName(),
                        post.getMemberId(),
                        authorInfoDTO.withdrawn() ? "탈퇴한 사용자" : authorInfoDTO.nickname(),
                        post.getTitle(),
                        post.getViewCount(),
                        post.getCommentCount(),
                        post.getLikeCount(),
                        post.getDislikeCount(),
                        post.isPopular(),
                        post.getCreatedAt()
                )
        );

        // 5. 페이징 응답 생성
        return new PageResponseDTO<>(
                content.getContent(),
                content.getNumber(),
                content.getSize(),
                content.getTotalElements(),
                content.getTotalPages()
        );
    }
}
