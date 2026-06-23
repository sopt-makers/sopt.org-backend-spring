package sopt.org.homepage.application.admin.dto.request.main.branding;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "브랜딩 컬러")
@Getter
@NoArgsConstructor
public class AddAdminBrandingColorRequestDto {
    @Schema(description = "다크모드 키 컬러", example = "#FF0000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "darkModeKeyColor must not be null")
    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "올바른 HEX 컬러 코드를 입력해주세요 (예: #FF0000)")
    private String darkModeKeyColor;

    @Schema(description = "다크모드 텍스트 컬러", example = "white", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "darkModeTextColor must not be null")
    @Pattern(regexp = "^(white|black)$", message = "텍스트 컬러는 white 또는 black이어야 합니다")
    private String darkModeTextColor;

    @Schema(description = "라이트모드 키 컬러", example = "#FF0000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "lightModeKeyColor must not be null")
    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "올바른 HEX 컬러 코드를 입력해주세요 (예: #FF0000)")
    private String lightModeKeyColor;

    @Schema(description = "라이트모드 텍스트 컬러", example = "black", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "lightModeTextColor must not be null")
    @Pattern(regexp = "^(white|black)$", message = "텍스트 컬러는 white 또는 black이어야 합니다")
    private String lightModeTextColor;

}
