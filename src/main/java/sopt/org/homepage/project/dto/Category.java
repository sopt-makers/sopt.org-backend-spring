package sopt.org.homepage.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.org.homepage.project.dto.type.ProjectType;

public record Category(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProjectType project
) {
}
