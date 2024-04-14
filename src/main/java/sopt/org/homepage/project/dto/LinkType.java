package sopt.org.homepage.project.dto;
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

    public static LinkType fromValue(String value) {
        for (LinkType type : values()) {
            if (type.toString().equalsIgnoreCase(value)) {
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



