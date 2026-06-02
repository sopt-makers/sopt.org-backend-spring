package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "어드민 모집안내 탭 배포 응답 (S3 PresignedUrl 포함)")
@Getter
@Builder
@RequiredArgsConstructor
public class AddAdminRecruitResponseDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "36")
    private final int generation;

    @Schema(description = "모집안내 헤더 이미지 S3 PresignedUrl", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String recruitHeaderImage;
}
