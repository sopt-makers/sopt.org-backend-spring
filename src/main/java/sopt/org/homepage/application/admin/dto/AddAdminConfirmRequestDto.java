package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "어드민 배포 확인")
@Getter
@RequiredArgsConstructor
public class AddAdminConfirmRequestDto {
    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    @NotEmpty(message = "generation must not be empty")
    @Positive(message = "generation must be a positive number")
    private int generation;
}


