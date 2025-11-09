package sopt.org.homepage.member.service.command.dto;

import lombok.Builder;
import sopt.org.homepage.member.domain.Member;
import sopt.org.homepage.member.domain.vo.MemberRole;
import sopt.org.homepage.member.domain.vo.SnsLinks;

/**
 * CreateMemberCommand
 *
 * 운영진 생성 커맨드
 */
@Builder
public record CreateMemberCommand(
        Integer generationId,
        MemberRole role,
        String name,
        String affiliation,
        String introduction,
        String profileImageUrl,
        SnsLinksCommand snsLinks
) {
    public Member toEntity() {
        return Member.builder()
                .generationId(generationId)
                .role(role)
                .name(name)
                .affiliation(affiliation)
                .introduction(introduction)
                .profileImageUrl(profileImageUrl)
                .snsLinks(snsLinks != null ? snsLinks.toVO() : SnsLinks.empty())
                .build();
    }

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