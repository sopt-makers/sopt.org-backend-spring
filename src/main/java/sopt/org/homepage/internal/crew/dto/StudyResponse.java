package sopt.org.homepage.internal.crew.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record StudyResponse(
        @Schema(description = "id", nullable = false)
        Long id,

        @Schema(description = "기수", nullable = true)
        Integer generation,

        @Schema(description = "스터디 관련 파트", nullable = false)
        List<MeetingJoinablePart> parts,

        @Schema(description = "스터디 명", nullable = false)
        String title,

        @Schema(description = "스터디 사진", nullable = true)
        String imageUrl,

        @Schema(description = "스터디 시작 날짜", nullable = false)
        LocalDateTime startDate,

        @Schema(description = "스터디 종료 날짜", nullable = false)
        LocalDateTime endDate,

        @Schema(description = "참여 인원 수", nullable = false)
        Integer memberCount
) {}

