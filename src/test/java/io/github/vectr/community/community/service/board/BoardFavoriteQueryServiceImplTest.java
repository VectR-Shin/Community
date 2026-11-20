package io.github.vectr.community.community.service.board;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.dto.board.BoardSummaryResponseDTO;
import io.github.vectr.community.community.repository.board.BoardFavoriteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardFavoriteQueryServiceImplTest {

    @Mock
    private BoardFavoriteRepository boardFavoriteRepository;

    @InjectMocks
    private BoardFavoriteQueryServiceImpl boardFavoriteQueryService;

    @Test
    @DisplayName("사용자가 즐겨찾기한 게시판 목록을 조회한다.")
    void getFavoriteBoards_success() {
        // given
        Long memberId = 1L;
        int page = 0;
        int size = 20;

        List<BoardSummaryResponseDTO> content = List.of(
                new BoardSummaryResponseDTO(
                        1L, "일반 게시판", 1L, "일반"
                ),
                new BoardSummaryResponseDTO(
                        2L, "질문 게시판", 1L, "일반"
                )
        );

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.asc("createdAt"),
                        Sort.Order.asc("id")
                )
        );

        Page<BoardSummaryResponseDTO> favoriteBoards =
                new PageImpl<>(content, pageable, content.size());

        when(boardFavoriteRepository.findFavoriteBoardsByMemberId(
                eq(memberId),
                any(Pageable.class)
        )).thenReturn(favoriteBoards);

        // when
        PageResponseDTO<BoardSummaryResponseDTO> result =
                boardFavoriteQueryService.getFavoriteBoards(
                        memberId,
                        page,
                        size
                );

        // then
        assertThat(result.content()).hasSize(2);

        assertThat(result.content().get(0).boardId()).isEqualTo(1L);
        assertThat(result.content().get(0).boardName()).isEqualTo("일반 게시판");

        assertThat(result.content().get(1).boardId()).isEqualTo(2L);
        assertThat(result.content().get(1).boardName()).isEqualTo("질문 게시판");

        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(20);
        assertThat(result.page()).isEqualTo(0);

        verify(boardFavoriteRepository)
                .findFavoriteBoardsByMemberId(eq(memberId), any(Pageable.class));
    }

    @Test
    @DisplayName("요청한 페이지와 크기 및 정렬 조건을 Repository에 전달한다.")
    void getFavoriteBoards_pageable() {
        // given
        Long memberId = 1L;
        int page = 1;
        int size = 5;

        when(boardFavoriteRepository.findFavoriteBoardsByMemberId(
                eq(memberId),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        // when
        boardFavoriteQueryService.getFavoriteBoards(memberId, page, size);

        // then
        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(boardFavoriteRepository).findFavoriteBoardsByMemberId(
                eq(memberId),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);

        assertThat(pageable.getSort().getOrderFor("createdAt").getDirection())
                .isEqualTo(Sort.Direction.ASC);

        assertThat(pageable.getSort().getOrderFor("id").getDirection())
                .isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("즐겨찾기한 게시판이 없으면 빈 목록을 반환한다.")
    void getFavoriteBoards_empty() {
        // given
        Long memberId = 1L;
        int page = 0;
        int size = 20;

        Pageable pageable = PageRequest.of(page, size);

        when(boardFavoriteRepository.findFavoriteBoardsByMemberId(
                eq(memberId),
                any(Pageable.class)
        )).thenReturn(Page.empty(pageable));

        // when
        PageResponseDTO<BoardSummaryResponseDTO> result =
                boardFavoriteQueryService.getFavoriteBoards(
                        memberId,
                        page,
                        size
                );

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();

        verify(boardFavoriteRepository)
                .findFavoriteBoardsByMemberId(eq(memberId), any(Pageable.class));
    }
}