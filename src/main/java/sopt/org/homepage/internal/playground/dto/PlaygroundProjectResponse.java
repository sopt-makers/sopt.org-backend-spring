package sopt.org.homepage.internal.playground.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.org.homepage.project.dto.*;

import java.util.List;

public record PlaygroundProjectResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer generation,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProjectType category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ServiceType> serviceType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String summary,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String detail,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoImage,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String thumbnailImage,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isAvailable,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isFounding,

        List<ProjectLinkResponse> links
) {
        public record ProjectLinkResponse(
            Long linkId,
            String linkTitle,
            String linkUrl
        ){}
        public PlaygroundProjectResponse ProjectWithLink(List<ProjectLinkResponse> links) {
                return new PlaygroundProjectResponse(
                        id(),
                        name(),
                        generation(),
                        category(),
                        serviceType(),
                        summary(),
                        detail(),
                        logoImage(),
                        thumbnailImage(),
                        isAvailable(),
                        isFounding(),
                        links
                );
        }
}
