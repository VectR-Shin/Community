package io.github.vectr.community.member.dto.profile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequestDTO(
        @Size(min = 4, max = 20, message = "{validation.nickname.size}")
        @Pattern(regexp = "^\\S+$", message = "{validation.nickname.noWhitespace}")
        String nickname,

        Boolean isPublic
) {}