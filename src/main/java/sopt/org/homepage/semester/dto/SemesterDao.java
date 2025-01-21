package sopt.org.homepage.semester.dto;

import com.querydsl.core.annotations.QueryProjection;

public record SemesterDao(
        Integer id,
        String color,
        String logo,
        String background,
        String name,
        String year
) {
    @QueryProjection
    public SemesterDao {}
}
