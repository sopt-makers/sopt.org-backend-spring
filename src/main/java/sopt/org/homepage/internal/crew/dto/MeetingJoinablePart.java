package sopt.org.homepage.internal.crew.dto;

import lombok.Getter;

@Getter
public enum MeetingJoinablePart {
    PM("PM"),
    DESIGN("DESIGN"),
    IOS("IOS"),
    ANDROID("ANDROID"),
    SERVER("SERVER"),
    WEB("WEB");

    private final String value;

    MeetingJoinablePart(String value) {
        this.value = value;
    }

}
