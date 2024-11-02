package sopt.org.homepage.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Validated
@Schema(description = "최신소식 삭제하기")
@Getter
@RequiredArgsConstructor
public class DeleteMainNewsRequestDto {
    @Schema(description = "최신소식 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "id must not be null")
    private final int id;
}


