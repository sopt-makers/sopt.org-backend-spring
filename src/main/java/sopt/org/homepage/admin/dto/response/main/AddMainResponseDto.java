package sopt.org.homepage.admin.dto.response.main;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.admin.dto.response.main.core.AddMainCoreValueResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.member.AddMainMemberResponseRecordDto;

import java.util.List;

@Validated
@Schema(description = "어드민 메인정보 추가")
@Getter
@Builder
@RequiredArgsConstructor
public class AddMainResponseDto {
    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    private final int generation;

    @Schema(description = "헤더 이미지 S3 PresignedUrl", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://image.url")
    private final String headerImage;

    @Schema(description = "핵심가치 이미지 S3 PresigneUrl 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private final List<AddMainCoreValueResponseRecordDto> coreValues;

    @Schema(description = "멤버 프로필 이미지 S3 PresigneUrl 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private final List<AddMainMemberResponseRecordDto> members;

    @Schema(description = "지원하기 헤더 이미지 S3 PresignedUrl", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://image.url")
    private final String recruitHeaderImage;
}


