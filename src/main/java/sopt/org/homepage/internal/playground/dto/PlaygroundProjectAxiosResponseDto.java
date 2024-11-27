package sopt.org.homepage.internal.playground.dto;

import java.util.List;


public record PlaygroundProjectAxiosResponseDto(
        List<PlaygroundProjectResponseDto> projectList,
        Boolean hasNext,
        Integer totalCount
) {}
