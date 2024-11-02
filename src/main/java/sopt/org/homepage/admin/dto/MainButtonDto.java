package sopt.org.homepage.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "메인 버튼 스타일")
@Getter
@NoArgsConstructor
public class MainButtonDto {
    @Schema(description = "버튼 텍스트", example = "지원하기", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "버튼 텍스트를 입력해주세요")
    private String text;

    @Schema(description = "주요 컬러", example = "#FF0000", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "올바른 HEX 컬러 코드를 입력해주세요")
    private String keyColor;

    @Schema(description = "보조 컬러", example = "#CC0000", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "올바른 HEX 컬러 코드를 입력해주세요")
    private String subColor;
}
