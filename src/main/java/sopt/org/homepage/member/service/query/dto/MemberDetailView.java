package sopt.org.homepage.member.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.member.domain.Member;
import sopt.org.homepage.member.domain.vo.MemberRole;

/**
 * MemberDetailView
 *
 * 운영진 상세 조회 DTO
 */
@Builder
public record MemberDetailView(
        Long id,
        Integer generationId,
        String role,  // 레거시 호환용 문자열
        String name,
        String affiliation,
        String introduction,
        String profileImageUrl,
        SnsLinksView snsLinks
) {
    public static MemberDetailView from(Member member) {
        return MemberDetailView.builder()
                .id(member.getId())
                .generationId(member.getGenerationId())
                .role(member.getRole().getDisplayName())  // Enum → 한글 문자열
                .name(member.getName())
                .affiliation(member.getAffiliation())
                .introduction(member.getIntroduction())
                .profileImageUrl(member.getProfileImageUrl())
                .snsLinks(SnsLinksView.from(member.getSnsLinks()))
                .build();
    }

    /**
     * 레거시 API 호환용 (profileImage 필드명)
     */
    public String getProfileImage() {
        return this.profileImageUrl;
    }

    @Builder
    public record SnsLinksView(
            String email,
            String linkedin,
            String github,
            String behance
    ) {
        public static SnsLinksView from(sopt.org.homepage.member.domain.vo.SnsLinks snsLinks) {
            if (snsLinks == null) {
                return SnsLinksView.builder().build();
            }
            return SnsLinksView.builder()
                    .email(snsLinks.getEmail())
                    .linkedin(snsLinks.getLinkedin())
                    .github(snsLinks.getGithub())
                    .behance(snsLinks.getBehance())
                    .build();
        }
    }
}