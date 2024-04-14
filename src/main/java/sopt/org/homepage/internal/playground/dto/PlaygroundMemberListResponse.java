package sopt.org.homepage.internal.playground.dto;

import java.util.List;

public record PlaygroundMemberListResponse(
        List<PlaygroundMemberResponse> members,
        Integer numberOfMembersAtGeneration
) {
}
