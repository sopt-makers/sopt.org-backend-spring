package sopt.org.homepage.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record Category(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProjectType project
) {
}
