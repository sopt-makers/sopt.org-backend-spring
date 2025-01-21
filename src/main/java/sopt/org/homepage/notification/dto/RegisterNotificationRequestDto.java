package sopt.org.homepage.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
public class RegisterNotificationRequestDto {

    @Schema(description = "활동 기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    @NotEmpty(message = "Generation must not be empty")
    @Positive(message = "Generation must be a positive number")
    private final int generation;

    @Schema(description = "이메일", requiredMode = Schema.RequiredMode.REQUIRED, example = "example@naver.com")
    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    private final String email;

    public RegisterNotificationRequestDto(int generation, String email) {
        this.generation = generation;
        this.email = email;
    }
}

