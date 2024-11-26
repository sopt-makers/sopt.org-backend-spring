package sopt.org.homepage.internal.playground.dto;

import java.util.List;
import lombok.Getter;


public record PlaygroundProjectAxiosResponseDto(
        List<PlaygroundProjectResponse> projectList,
        Boolean hasNext,
        Integer totalCount
) {}
