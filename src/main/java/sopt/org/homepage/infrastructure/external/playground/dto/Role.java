package sopt.org.homepage.infrastructure.external.playground.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    TEAMLEADER("Team Leader"),
    MAINPM("Main PM"),
    PM("PM"),
    TEAMIMPROVEMENT("Team Improvement"),
    DESIGN("디자이너"),
    IOS("iOS 개발자"),
    ANDROID("Android 개발자"),
    WEB("웹 프론트엔드 개발자"),
    SERVER("서버 개발자");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    @JsonValue // Jackson이 JSON 직렬화 시 이 메서드를 사용하도록 지정
    public String getDescription() {
        return description;
    }


    public static Role fromValue(String value) {
        // enum 이름으로 먼저 찾기
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }

        // description으로 찾기
        for (Role role : values()) {
            if (role.getDescription().equalsIgnoreCase(value)) {
                return role;
            }
        }

        throw new IllegalArgumentException("No constant with text " + value + " found");
    }

}
