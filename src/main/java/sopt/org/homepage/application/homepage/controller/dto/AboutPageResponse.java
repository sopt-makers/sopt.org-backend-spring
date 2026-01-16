package sopt.org.homepage.application.homepage.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

/**
 * AboutPageResponse
 * <p>
 * GET /homepage/about 응답 DTO
 */
@Schema(description = "About 페이지 데이터")
@Builder
public record AboutPageResponse(
        @Schema(description = "기수", example = "35")
        int generation,

        @Schema(description = "기수명", example = "35기")
        String name,

        @Schema(description = "헤더 이미지 URL")
        String headerImage,

        BrandingColor brandingColor,
        List<CoreValue> coreValue,
        List<PartCurriculum> partCurriculum,
        List<Member> member
) {
    @Builder
    public record BrandingColor(
            String main,
            String high,
            String low,
            String point
    ) {
    }

    @Builder
    public record CoreValue(
            String value,
            String description,
            String image
    ) {
    }

    @Builder
    public record PartCurriculum(
            String part,
            List<String> curriculums
    ) {
    }

    @Builder
    public record Member(
            String role,
            String name,
            String affiliation,
            String introduction,
            String profileImage,
            SnsLinks sns
    ) {
        @Builder
        public record SnsLinks(
                String email,
                String linkedin,
                String github,
                String behance
        ) {
        }
    }
}
