package sopt.org.homepage.faq.vo;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.homepage.global.common.type.PartType;

/**
 * FAQCategory Enum
 * <p>
 * FAQ 카테고리 구분 - 신규 카테고리는 선언 순서대로 응답 - 레거시 상수는 기존 파트 FAQ(recruitQuestion) 호환용
 */
@Getter
@RequiredArgsConstructor
public enum FAQCategory {
    QUALIFICATION("지원자격", false),
    INTERVIEW("면접", false),
    ACTIVITY("활동", false),

    // 레거시 필드 정리 시 삭제 예정
    IOS("iOS", true),
    PLAN("기획", true),
    DESIGN("디자인", true),
    SERVER("서버", true),
    ANDROID("안드로이드", true),
    WEB("웹", true),
    COMMON("공통", true);

    private final String displayName;
    private final boolean legacy;

    public static List<FAQCategory> legacyValues() {
        return Arrays.stream(values())
                .filter(FAQCategory::isLegacy)
                .toList();
    }

    public static List<FAQCategory> currentValues() {
        return Arrays.stream(values())
                .filter(category -> !category.legacy)
                .toList();
    }

    /**
     * 기존 파트를 같은 이름의 레거시 FAQCategory로 변환
     */
    public static FAQCategory fromPartType(PartType partType) {
        return valueOf(partType.name());
    }

    /**
     * 화면 표시 이름을 신규 FAQCategory로 변환
     */
    public static FAQCategory fromString(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("FAQ category must not be blank");
        }

        for (FAQCategory category : currentValues()) {
            if (category.displayName.equals(displayName.trim())) {
                return category;
            }
        }

        throw new IllegalArgumentException("Unknown FAQ category: " + displayName);
    }
}
