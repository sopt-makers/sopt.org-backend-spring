package sopt.org.homepage.internal.playground.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import sopt.org.homepage.project.dto.type.ProjectType;
import sopt.org.homepage.project.dto.type.ServiceType;


public record PlaygroundProjectResponseDto(
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
        public PlaygroundProjectResponseDto ProjectWithLink(List<ProjectLinkResponse> links) {
                return new PlaygroundProjectResponseDto(
                        id, name, generation, category, serviceType,
                        summary, detail, logoImage, thumbnailImage,
                        isAvailable, isFounding, links
                );
        }
}
