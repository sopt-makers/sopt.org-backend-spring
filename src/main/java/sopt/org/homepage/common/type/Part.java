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

    @Override
    public String toString() {
        return this.value;
    }
}
