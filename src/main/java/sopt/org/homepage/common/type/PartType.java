package sopt.org.homepage.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
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


    @JsonCreator  //  역직렬화: "SERVER" → PartType.SERVER
    public static PartType fromJson(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("PartType은 필수입니다.");
        }

        // 1. enum 상수명으로 매칭 (SERVER, WEB, ...)
        for (PartType type : values()) {
            if (type.name().equalsIgnoreCase(input)) {
                return type;
            }
        }

        // 2. 한글 값으로 매칭 (서버, 웹, ...)
        for (PartType type : values()) {
            if (type.value.equalsIgnoreCase(input)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                "Unknown PartType: " + input +
                        ". 가능한 값: SERVER, WEB, ANDROID, iOS, PLAN, DESIGN, COMMON " +
                        "또는 서버, 웹, 안드로이드, iOS, 기획, 디자인, 공통"
        );
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
