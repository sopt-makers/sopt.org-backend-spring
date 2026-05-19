package sopt.org.homepage.application.admin.dto.request.main.activityschedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddAdminActivityScheduleRequestDto {

    @Schema(description = "일정명", example = "OT")
    @NotBlank
    private final String name;

    @Schema(description = "날짜 (yyyy-MM-dd)", example = "2026-03-28")
    @NotBlank
    private final String date;
}
