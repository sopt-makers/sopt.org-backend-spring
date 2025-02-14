package sopt.org.homepage.admin.dto.request.main.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.main.entity.sub.SnsLinksEntity;

@Schema(description = "SNS 링크 정보")
@Getter
@NoArgsConstructor
class AddAdminSnsLinksRequestDto {
    @Schema(description = "이메일", example = "example@sopt.org")
    @Email(message = "올바른 이메일 형식을 입력해주세요")
    private String email;

    @Schema(description = "링크드인 URL", example = "https://www.linkedin.com/in/example")
    @Pattern(regexp = "^https?://.*", message = "올바른 URL을 입력해주세요")
    private String linkedin;

    @Schema(description = "깃허브 URL", example = "https://github.com/example")
    @Pattern(regexp = "^https?://.*", message = "올바른 URL을 입력해주세요")
    private String github;

    @Schema(description = "비핸스 URL", example = "https://www.behance.net/example")
    @Pattern(regexp = "^https?://.*", message = "올바른 URL을 입력해주세요")
    private String behance;

    public SnsLinksEntity toEntity() {
        return SnsLinksEntity.builder()
                .email(this.email)
                .linkedin(this.linkedin)
                .github(this.github)
                .behance(this.behance)
                .build();
    }
}
