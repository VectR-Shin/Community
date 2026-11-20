package io.github.vectr.community.community.entity.board;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "board_favorite",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_board_favorite_board_member",
                        columnNames = {"board_id", "member_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardFavorite extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    public BoardFavorite(Board board, Long memberId) {
        this.board = Objects.requireNonNull(board);
        this.memberId = Objects.requireNonNull(memberId);
    }
}
