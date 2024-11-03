package sopt.org.homepage.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Validated
@Schema(description = "어드민 배포 확인")
@Getter
@RequiredArgsConstructor
public class AddMainConfirmRequestDto {
    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "34")
    @NotEmpty(message = "generation must not be empty")
    @Positive(message = "generation must be a positive number")
    private int generation;
}


