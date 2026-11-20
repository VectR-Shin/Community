package io.github.vectr.community.community.entity.board;

import io.github.vectr.community.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "board_category",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_board_category_name",
                        columnNames = "name"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    public BoardCategory(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public void changeName(String name) {
        this.name = Objects.requireNonNull(name);
    }
}