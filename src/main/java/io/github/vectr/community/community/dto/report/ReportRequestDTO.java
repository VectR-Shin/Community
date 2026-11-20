package io.github.vectr.community.community.dto.report;

import io.github.vectr.community.community.enums.ReportReason;
import io.github.vectr.community.community.enums.ReportTargetType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReportRequestDTO(
        @NotNull(message = "{validation.report.targetId.notNull}")
        @Positive(message = "{validation.report.targetId.positive}")
        Long targetId,

        @NotNull(message = "{validation.report.targetType.notNull}")
        ReportTargetType targetType,

        @NotNull(message = "{validation.report.reason.notNull}")
        ReportReason reason,

        @Size(max = 500, message = "{validation.report.content.size}")
        String content
) {}
