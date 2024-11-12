package sopt.org.homepage.main.dto.response.main.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "SNS 링크 정보")
@Builder
public record GetMainSnsLinksResponseRecordDto(
        @Schema(description = "이메일", example = "example@sopt.org") String email,
        @Schema(description = "링크드인 URL", example = "https://www.linkedin.com/in/example") String linkedin,
        @Schema(description = "깃허브 URL", example = "https://github.com/example") String github,
        @Schema(description = "비핸스 URL", example = "https://www.behance.net/example") String behance
) {}

