package sopt.org.homepage.news.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Schema(description = "최신소식 삭제하기")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAdminNewsRequestDto {
    @Schema(description = "최신소식 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "id must not be null")
    private int id;
}


