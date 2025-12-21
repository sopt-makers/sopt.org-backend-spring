package sopt.org.homepage.application.admin.dto.request.main.recruit.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Schema(description = "모집 일정")
@Getter
@NoArgsConstructor
public class AddAdminRecruitScheduleRequestDto {
    @Schema(description = "타입", example = "OB", allowableValues = {"OB",
            "YB"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^(OB|YB)$", message = "타입은 OB 또는 YB만 가능합니다")
    private String type;

    @Schema(description = "일정", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private AddAdminScheduleRequestDto schedule;

}

