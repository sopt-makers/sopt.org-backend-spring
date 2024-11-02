package sopt.org.homepage.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "멤버 정보")
@Getter
@NoArgsConstructor
public class MemberDto {
    @Schema(description = "역할", example = "회장", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "역할을 입력해주세요")
    private String role;

    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @Schema(description = "소속", example = "SOPT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "소속을 입력해주세요")
    private String affiliation;

    @Schema(description = "한줄 소개", example = "안녕하세요!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "한줄 소개를 입력해주세요")
    private String introduction;

    @Schema(description = "SNS 링크", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private SnsLinksDto sns;
}

