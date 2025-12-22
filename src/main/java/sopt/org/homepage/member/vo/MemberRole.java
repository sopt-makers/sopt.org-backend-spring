package sopt.org.homepage.member.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * MemberRole Enum
 * <p>
 * SOPT 운영진 역할 정의
 */

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    PRESIDENT("회장"),
    VICE_PRESIDENT("부회장"),
    GENERAL_AFFAIRS("총무"),
    OPERATION_TEAM_LEADER("운영 팀장"),
    MEDIA_TEAM_LEADER("미디어 팀장"),
    MAKERS_TEAM_LEADER("메이커스 팀장"),
    PLANNING_TEAM_LEADER("기획 팀장"),
    DESIGN_TEAM_LEADER("디자인 팀장"),
    ANDROID_LEADER("안드로이드 파트장"),
    IOS_LEADER("iOS 파트장"),
    WEB_LEADER("웹 파트장"),
    SERVER_LEADER("서버 파트장"),
    PLAN_LEADER("기획 파트장"),
    DESIGN_LEADER("디자인 파트장"),
    MEDIA_TEAM_MEMBER("미디어팀"),
    PLANNING_TEAM_MEMBER("기획팀"),
    DESIGN_TEAM_MEMBER("디자인팀");

    private final String displayName;

    /**
     * 레거시 role 문자열을 MemberRole로 매핑
     *
     * @param role 레거시 역할 문자열
     * @return MemberRole enum
     */
    public static MemberRole fromLegacyRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role must not be blank");
        }

        // 레거시 매핑 (기존 "회장", "부회장" 등의 문자열 그대로 사용)
        return switch (role) {
            case "회장" -> PRESIDENT;
            case "부회장" -> VICE_PRESIDENT;
            case "총무" -> GENERAL_AFFAIRS;
            case "운영 팀장" -> OPERATION_TEAM_LEADER;
            case "미디어 팀장" -> MEDIA_TEAM_LEADER;
            case "메이커스 팀장" -> MAKERS_TEAM_LEADER;
            case "기획", "기획 파트장" -> PLANNING_TEAM_LEADER;
            case "디자인", "디자인 팀장", "디자인 파트장" -> DESIGN_TEAM_LEADER;
            case "안드로이드", "안드로이드 파트장" -> ANDROID_LEADER;
            case "iOS", "iOS 파트장" -> IOS_LEADER;
            case "웹", "웹 파트장" -> WEB_LEADER;
            case "서버", "서버 파트장" -> SERVER_LEADER;
            case "미디어팀" -> MEDIA_TEAM_MEMBER;
            case "기획팀" -> PLANNING_TEAM_MEMBER;
            case "디자인팀" -> DESIGN_TEAM_MEMBER;
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}
