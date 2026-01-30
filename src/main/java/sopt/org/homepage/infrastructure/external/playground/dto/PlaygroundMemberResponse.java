package sopt.org.homepage.infrastructure.external.playground.dto;

public record PlaygroundMemberResponse(
        Long id,
        String name,
        String profileImage,
        String introduction,
        String part,
        Integer generation,
        Boolean allowOfficial
) {
}
