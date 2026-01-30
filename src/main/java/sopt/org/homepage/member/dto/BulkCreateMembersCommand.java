package sopt.org.homepage.member.dto;

import java.util.List;
import lombok.Builder;

/**
 * BulkCreateMembersCommand
 * <p>
 * 운영진 일괄 생성 커맨드 (Admin용)
 */
@Builder
public record BulkCreateMembersCommand(
        Integer generationId,
        List<MemberData> members
) {
    @Builder
    public record MemberData(
            String role,  // 레거시 문자열 role
            String name,
            String affiliation,
            String introduction,
            String profileImageUrl,
            SnsLinksData snsLinks
    ) {
    }

    @Builder
    public record SnsLinksData(
            String email,
            String linkedin,
            String github,
            String behance
    ) {
    }
}
