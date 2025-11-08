package sopt.org.homepage.common.type;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PartType {
    IOS("iOS"),
    PLAN("기획"),
    DESIGN("디자인"),
    SERVER("서버"),
    ANDROID("안드로이드"),
    WEB("웹"),
    COMMON("공통");

    private final String value;


    @JsonValue
    public String getValue() {
        return value;
    }


    public static PartType fromValue(String value) {
        for (PartType partType : values()) {
            if (partType.getValue().equalsIgnoreCase(value)) {
                return partType;
            }
        }
        throw new IllegalArgumentException("Unknown PartType value: " + value);
    }

    public static PartType fromString(String partName) {
        if (partName == null || partName.isBlank()) {
            throw new IllegalArgumentException("PartType name must not be blank");
        }

        return switch (partName.trim()) {
            case "안드로이드", "Android", "ANDROID" -> ANDROID;
            case "iOS", "IOS", "아이오에스" -> IOS;
            case "웹", "Web", "WEB" -> WEB;
            case "서버", "Server", "SERVER" -> SERVER;
            case "기획", "Plan", "PLAN" -> PLAN;
            case "디자인", "Design", "DESIGN" -> DESIGN;
            default -> throw new IllegalArgumentException("Unknown partType: " + partName);
        };
    }

    @Override
    public String toString() {
        return this.value;
    }
}
