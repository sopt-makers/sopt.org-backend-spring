package sopt.org.homepage.member.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.member.domain.Member;

/**
 * MemberListView
 *
 * 운영진 목록 조회 DTO (간략 정보)
 */
@Builder
public record MemberListView(
        Long id,
        String role,
        String name,
        String profileImageUrl
) {
    public static MemberListView from(Member member) {
        return MemberListView.builder()
                .id(member.getId())
                .role(member.getRole().getDisplayName())
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }

    /**
     * 레거시 API 호환용
     */
    public String getProfileImage() {
        return this.profileImageUrl;
    }
}