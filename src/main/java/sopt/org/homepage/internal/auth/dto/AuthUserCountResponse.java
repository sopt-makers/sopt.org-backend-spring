package sopt.org.homepage.internal.auth.dto;

public record AuthUserCountResponse(
        boolean success,
        String message,
        AuthUserCountData data
) {
    public record AuthUserCountData(
            int numberOfMembersAtGeneration
    ) {}
}