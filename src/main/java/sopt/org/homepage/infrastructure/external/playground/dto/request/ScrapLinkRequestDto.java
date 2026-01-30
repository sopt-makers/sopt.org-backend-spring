package sopt.org.homepage.infrastructure.external.playground.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "블로그 링크 정보 스크랩 요청")
public class ScrapLinkRequestDto {

    @Schema(description = "블로그 주소", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "블로그 주소는 필수입니다")
    private String link;
}
