package sopt.org.homepage.recruitment.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * RecruitType Enum
 * <p>
 * 모집 대상 구분
 */
@Getter
@RequiredArgsConstructor
public enum RecruitType {
    OB("OB", "기존 회원"),
    YB("YB", "신규 회원");

    private final String code;
    private final String displayName;

    /**
     * 레거시 문자열을 RecruitType으로 변환
     */
    public static RecruitType fromString(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Recruit type must not be blank");
        }

        return switch (type.trim().toUpperCase()) {
            case "OB" -> OB;
            case "YB" -> YB;
            default -> throw new IllegalArgumentException("Unknown recruit type: " + type);
        };
    }
}
