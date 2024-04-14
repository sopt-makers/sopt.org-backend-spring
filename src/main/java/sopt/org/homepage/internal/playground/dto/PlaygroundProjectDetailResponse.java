package sopt.org.homepage.internal.playground.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.org.homepage.project.dto.ProjectType;
import sopt.org.homepage.project.dto.ServiceType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PlaygroundProjectDetailResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Long writerId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Integer generation,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProjectType category,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate startAt,
        LocalDate endAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ServiceType> serviceType,
        Boolean isAvailable,
        Boolean isFounding,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String summary,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String detail,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String logoImage,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        String thumbnailImage,
        List<String> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProjectMemberResponse> members,
        List<ProjectLinkResponse> links
) {
    public record ProjectMemberResponse(
            Long memberId,
            String memberRole,
            String memberDescription,
            Boolean isTeamMember,
            String memberName,
            List<Integer> memberGenerations,
            String memberProfileImage,
            Boolean memberHasProfile
    ){}

    public record ProjectLinkResponse(
            Long linkId,
            String linkTitle,
            String linkUrl
    ){}
}
