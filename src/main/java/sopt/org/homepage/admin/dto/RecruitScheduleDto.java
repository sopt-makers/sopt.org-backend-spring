package sopt.org.homepage.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.admin.dao.RecruitScheduleDao;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "모집 일정")
@Getter
@NoArgsConstructor
public class RecruitScheduleDto {
    @Schema(description = "타입", example = "OB", allowableValues = {"OB", "YB"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^(OB|YB)$", message = "타입은 OB 또는 YB만 가능합니다")
    private String type;

    @Schema(description = "일정", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private ScheduleDto schedule;

    public RecruitScheduleDao toDao() {
        return RecruitScheduleDao.builder()
                .type(this.type)
                .schedule(this.schedule.toDao())
                .build();
    }

    public static List<RecruitScheduleDao> toDaoList(List<RecruitScheduleDto> dtos) {
        return dtos.stream()
                .map(RecruitScheduleDto::toDao)
                .collect(Collectors.toList());
    }
}

