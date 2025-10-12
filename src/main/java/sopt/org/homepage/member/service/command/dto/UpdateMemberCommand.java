package sopt.org.homepage.member.service.command.dto;

import lombok.Builder;
import sopt.org.homepage.member.domain.vo.MemberRole;
import sopt.org.homepage.member.domain.vo.SnsLinks;

/**
 * UpdateMemberCommand
 *
 * 운영진 수정 커맨드
 */
@Builder
public record UpdateMemberCommand(
        Long id,
        MemberRole role,
        String name,
        String affiliation,
        String introduction,
        String profileImageUrl,
        SnsLinksCommand snsLinks
) {
    @Builder
    public record SnsLinksCommand(
            String email,
            String linkedin,
            String github,
            String behance
    ) {
        public SnsLinks toVO() {
            return SnsLinks.builder()
                    .email(email)
                    .linkedin(linkedin)
                    .github(github)
                    .behance(behance)
                    .build();
        }
    }
}