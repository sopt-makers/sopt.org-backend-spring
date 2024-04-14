package sopt.org.homepage.internal.playground.dto;

public record PlaygroundMemberResponse(
        Long id,
        String name,
        String profileImage,
        String introduction,
        String part,
        Integer generation,
        Boolean allowOfficial
) {}
