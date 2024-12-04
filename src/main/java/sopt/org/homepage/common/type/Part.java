package sopt.org.homepage.common.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Part {
    iOS("iOS"),
    PLAN("PLAN"),
    DESIGN("DESIGN"),
    SERVER("SERVER"),
    ANDROID("ANDROID"),
    WEB("WEB");

    private final String value;

    Part(String value) {
        this.value = value;
    }

    @JsonValue // JSON 직렬화 시 이 메서드 사용
    public String getValue() {
        return value;
    }

    // String 값으로부터 enum 찾기 (대소문자 구분 없이)
    public static Part fromValue(String value) {
        for (Part part : values()) {
            if (part.getValue().equalsIgnoreCase(value)) {
                return part;
            }
        }
        throw new IllegalArgumentException("Unknown Part value: " + value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
