package sopt.org.homepage.infrastructure.external.auth.dto;

public record AuthUserCountResponse(
        boolean success,
        String message,
        AuthUserCountData data
) {
    public record AuthUserCountData(
            int numberOfMembersAtGeneration
    ) {
    }
}
