package sopt.org.homepage.infrastructure.external.playground.dto;

import java.util.List;

public record PlaygroundMemberListResponse(
        List<PlaygroundMemberResponse> members,
        Integer numberOfMembersAtGeneration
) {
}
