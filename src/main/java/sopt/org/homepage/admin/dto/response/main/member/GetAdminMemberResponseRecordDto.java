package sopt.org.homepage.admin.dto.response.main.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "멤버 정보")
@Builder
public record GetAdminMemberResponseRecordDto(
        @Schema(description = "역할", example = "회장", requiredMode = Schema.RequiredMode.REQUIRED) String role,
        @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED) String name,
        @Schema(description = "소속", example = "SOPT", requiredMode = Schema.RequiredMode.REQUIRED) String affiliation,
        @Schema(description = "한줄 소개", example = "안녕하세요!", requiredMode = Schema.RequiredMode.REQUIRED) String introduction,
        @Schema(description = "프로필 이미지 링크", example = "https://profile.png", requiredMode = Schema.RequiredMode.REQUIRED) String profileImage,
        @Schema(description = "SNS 링크", requiredMode = Schema.RequiredMode.REQUIRED) GetAdminSnsLinksResponseRecordDto sns
) {
}
