package sopt.org.homepage.internal.crew.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CrewMeetingList(
        List<CrewMeetingVo> meetings,
        PaginationMeta meta
) {}
