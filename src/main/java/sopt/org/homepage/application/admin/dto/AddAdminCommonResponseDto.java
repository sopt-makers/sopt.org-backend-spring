package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "어드민 공통 탭 배포 응답 (이미지 없음)")
@Getter
@Builder
@RequiredArgsConstructor
public class AddAdminCommonResponseDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "36")
    private final int generation;
}
