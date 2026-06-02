package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.homepage.application.admin.dto.response.main.core.AddAdminCoreValueResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.member.AddAdminMemberResponseRecordDto;

@Schema(description = "어드민 소개 탭 배포 응답 (S3 PresignedUrl 포함)")
@Getter
@Builder
@RequiredArgsConstructor
public class AddAdminAboutResponseDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "36")
    private final int generation;

    @Schema(description = "소개 헤더 이미지 S3 PresignedUrl", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String headerImage;

    @Schema(description = "핵심가치 이미지 S3 PresignedUrl 목록")
    private final List<AddAdminCoreValueResponseRecordDto> coreValues;

    @Schema(description = "임원진 프로필 이미지 S3 PresignedUrl 목록")
    private final List<AddAdminMemberResponseRecordDto> members;
}
