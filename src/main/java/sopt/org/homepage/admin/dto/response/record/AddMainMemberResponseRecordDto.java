package sopt.org.homepage.admin.dto.response.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "멤버 프로필 이미지 S3 PresigneUrl 정보")
@Builder
public record AddMainMemberResponseRecordDto(
        @Schema(description = "역할", example = "회장", requiredMode = Schema.RequiredMode.REQUIRED) String role,
        @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED) String name,
        @Schema(description = "프로필 이미지 PresgiendUrl", requiredMode = Schema.RequiredMode.REQUIRED) String profileImage) {
}

