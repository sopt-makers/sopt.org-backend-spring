package sopt.org.homepage.semester.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SemestersListResponse(

        @Schema(description = "페이지네이션 조회 조건 페이지 수")
        Integer page,

        @Schema(description = "페이지네이션 조회로 기수 정보 가져올 갯수")
        Integer limit,

        @Schema(description = "조회한 기수 리스트 수")
        Long total,
        @Schema(description = "기수 대표 정보", nullable = true)
        List<SemestersResponse> semesters

) {
        public record SemestersResponse(
                @Schema(description = "역대 기수")
                Integer id,

                @Schema(description = "기수에 사용했던 컬러", nullable = true)
                String color,

                @Schema(description = "기수에 사용했던 로고")
                String logo,

                @Schema(description = "기수에 사용했던 백그라운드 이미지", nullable = true)
                String background,

                @Schema(description = "기수에 사용했던 테마명", nullable = true)
                String name,

                @Schema(description = "기수 활동 기간")
                String year
        ) {}
}

