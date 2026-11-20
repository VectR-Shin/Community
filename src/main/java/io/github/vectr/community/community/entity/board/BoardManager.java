package io.github.vectr.community.community.entity.board;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.community.enums.BoardManagerRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "board_manager",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_board_manager_board_member",
                        columnNames = {"board_id", "member_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardManager extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoardManagerRole role;

    public BoardManager(Board board, Long memberId, BoardManagerRole role) {
        this.board = Objects.requireNonNull(board);
        this.memberId = Objects.requireNonNull(memberId);
        this.role = Objects.requireNonNull(role);
    }

    public void changeRole(BoardManagerRole role) {
        this.role = Objects.requireNonNull(role);
    }
}