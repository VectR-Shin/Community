
package io.github.vectr.community.community.service.report;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.api.comment.CommentReportCommandService;
import io.github.vectr.community.community.api.post.PostReportCommandService;
import io.github.vectr.community.community.dto.report.ReportRequestDTO;
import io.github.vectr.community.community.enums.ReportTargetType;
import io.github.vectr.community.member.api.suspension.SuspensionQueryService;
import io.github.vectr.community.member.exception.MemberSuspensionErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportCommandServiceImplTest {

    @Mock
    private SuspensionQueryService suspensionQueryService;

    @Mock
    private CommentReportCommandService commentReportCommandService;

    @Mock
    private PostReportCommandService postReportCommandService;

    @InjectMocks
    private ReportCommandServiceImpl reportCommandService;

    @Test
    @DisplayName("게시글 신고를 정상적으로 위임한다.")
    void createReportForPost() {
        // given
        Long memberId = 1L;

        ReportRequestDTO requestDTO = mock(ReportRequestDTO.class);
        when(requestDTO.targetType()).thenReturn(ReportTargetType.POST);

        // when
        reportCommandService.createReport(memberId, requestDTO);

        // then
        verify(suspensionQueryService).validateNotBanned(memberId);

        verify(postReportCommandService).createPostReport(memberId, requestDTO);

        verifyNoInteractions(commentReportCommandService);
    }

    @Test
    @DisplayName("댓글 신고를 정상적으로 위임한다.")
    void createReportForComment() {
        // given
        Long memberId = 1L;

        ReportRequestDTO requestDTO = mock(ReportRequestDTO.class);
        when(requestDTO.targetType()).thenReturn(ReportTargetType.COMMENT);

        // when
        reportCommandService.createReport(memberId, requestDTO);

        // then
        verify(suspensionQueryService).validateNotBanned(memberId);

        verify(commentReportCommandService).createCommentReport(memberId, requestDTO);

        verifyNoInteractions(postReportCommandService);
    }

    @Test
    @DisplayName("전역 밴 상태이면 신고 생성에 실패한다")
    void createReportWhenMemberBanned() {
        // given
        Long memberId = 1L;
        ReportRequestDTO requestDTO = mock(ReportRequestDTO.class);

        doThrow(new BusinessException(MemberSuspensionErrorCode.MEMBER_BANNED))
                .when(suspensionQueryService)
                .validateNotBanned(memberId);

        // when & then
        assertThatThrownBy(() ->
                reportCommandService.createReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verify(suspensionQueryService).validateNotBanned(memberId);
        verifyNoInteractions(postReportCommandService, commentReportCommandService);
    }
}
