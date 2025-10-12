package sopt.org.homepage.common.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Part {
    IOS("IOS"),
    PLAN("PLAN"),
    DESIGN("DESIGN"),
    SERVER("SERVER"),
    ANDROID("ANDROID"),
    WEB("WEB");

    private final String value;


    @JsonValue
    public String getValue() {
        return value;
    }


    public static Part fromValue(String value) {
        for (Part part : values()) {
            if (part.getValue().equalsIgnoreCase(value)) {
                return part;
            }
        }
        throw new IllegalArgumentException("Unknown Part value: " + value);
    }

    public static Part fromString(String partName) {
        if (partName == null || partName.isBlank()) {
            throw new IllegalArgumentException("Part name must not be blank");
        }

        return switch (partName.trim()) {
            case "안드로이드", "Android", "ANDROID" -> ANDROID;
            case "iOS", "IOS", "아이오에스" -> IOS;
            case "웹", "Web", "WEB" -> WEB;
            case "서버", "Server", "SERVER" -> SERVER;
            case "기획", "Plan", "PLAN" -> PLAN;
            case "디자인", "Design", "DESIGN" -> DESIGN;
            default -> throw new IllegalArgumentException("Unknown part: " + partName);
        };
    }

    @Override
    public String toString() {
        return this.value;
    }
}
