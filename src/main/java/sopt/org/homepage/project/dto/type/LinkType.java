package sopt.org.homepage.project.dto.type;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum LinkType {
    WEBSITE("website"),
    GOOGLE_PLAYSTORE("googlePlay"),
    APP_STORE("appStore"),
    GITHUB("github"),
    MEDIA("media"),
    INSTAGRAM("instagram");

    private final String value;

    LinkType(String value) {
        this.value = value;
    }

    @JsonValue // Jackson이 JSON 직렬화 시 이 메서드를 사용하도록 지정
    public String getValue() {
        return value;
    }

    public static LinkType fromValue(String value) {
        for (LinkType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + value + " found");
    }

    @Override
    public String toString() {
        return this.value;
    }
}



