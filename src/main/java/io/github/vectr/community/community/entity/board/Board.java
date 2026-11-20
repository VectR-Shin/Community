package io.github.vectr.community.community.entity.board;

import io.github.vectr.community.common.entity.BaseCreatedAtEntity;
import io.github.vectr.community.community.enums.BoardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        name = "board",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_board_name",
                        columnNames = "name"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private BoardCategory category;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 100)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_status", nullable = false, length = 20)
    private BoardStatus boardStatus = BoardStatus.NORMAL;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Board(
            BoardCategory category,
            String name,
            String description
    ) {
        this.category = Objects.requireNonNull(category);
        this.name = Objects.requireNonNull(name);
        this.description = description;
    }

    public void changeCategory(BoardCategory category) {
        this.category = Objects.requireNonNull(category);
    }

    public void changeName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeStatus(BoardStatus boardStatus) {
        this.boardStatus = Objects.requireNonNull(boardStatus);
    }

    public void delete() {
        this.deletedAt = Instant.now();
    }

    public void restore() {
        this.deletedAt = null;
    }
}