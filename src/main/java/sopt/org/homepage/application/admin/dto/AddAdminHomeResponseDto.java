package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.homepage.application.admin.dto.response.main.news.AddAdminNewsResponseRecordDto;

@Schema(description = "어드민 홈 탭 배포 응답 (S3 PresignedUrl 포함)")
@Getter
@Builder
@RequiredArgsConstructor
public class AddAdminHomeResponseDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "36")
    private final int generation;

    @Schema(description = "홈 헤더 이미지 S3 PresignedUrl", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String homeHeaderImage;

    @Schema(description = "최신소식 이미지 S3 PresignedUrl 목록")
    private final List<AddAdminNewsResponseRecordDto> news;
}
