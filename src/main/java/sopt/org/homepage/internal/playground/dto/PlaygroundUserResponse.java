package sopt.org.homepage.internal.playground.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaygroundUserResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer latestGeneration,
        String profileImage,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean hasProfile
) {
}
