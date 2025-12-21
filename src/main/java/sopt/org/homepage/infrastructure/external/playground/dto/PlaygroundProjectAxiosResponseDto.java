package sopt.org.homepage.infrastructure.external.playground.dto;

import java.util.List;


public record PlaygroundProjectAxiosResponseDto(
        List<PlaygroundProjectResponseDto> projectList,
        Boolean hasNext,
        Integer totalCount
) {
}
