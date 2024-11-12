package sopt.org.homepage.main.dto.response.main.recruit.curriculum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "소개글 정보")
@Builder
public record GetMainIntroductionResponseRecordDto(
        @Schema(description = "내용", example = "Android 앱 개발", requiredMode = Schema.RequiredMode.REQUIRED) String content,
        @Schema(description = "우대사항", example = "Kotlin 개발 경험", requiredMode = Schema.RequiredMode.REQUIRED) String preference
) {
}
