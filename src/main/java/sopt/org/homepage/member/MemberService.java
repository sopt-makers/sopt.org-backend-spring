package sopt.org.homepage.member;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.member.dto.BulkCreateMembersCommand;
import sopt.org.homepage.member.dto.MemberDetailView;
import sopt.org.homepage.member.vo.MemberRole;
import sopt.org.homepage.member.vo.SnsLinks;

/**
 * MemberService
 * <p>
 * 통합 Service (Command + Query) - SOPT 운영진 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // ===== Command 메서드 =====

    /**
     * 운영진 일괄 생성 (기존 데이터 삭제 후 재생성) Admin에서 기수별 운영진 전체 교체 시 사용
     */
    @Transactional
    public List<Long> bulkCreate(BulkCreateMembersCommand command) {
        log.info("운영진 일괄 생성 - generationId={}", command.generationId());

        // 1. 기존 운영진 모두 삭제
        memberRepository.deleteByGenerationId(command.generationId());

        // 2. 새로운 운영진 생성
        List<Member> members = command.members().stream()
                .map(data -> Member.builder()
                        .generationId(command.generationId())
                        .role(MemberRole.fromLegacyRole(data.role()))
                        .name(data.name())
                        .affiliation(data.affiliation())
                        .introduction(data.introduction())
                        .profileImageUrl(data.profileImageUrl())
                        .snsLinks(data.snsLinks() != null ? SnsLinks.builder()
                                .email(data.snsLinks().email())
                                .linkedin(data.snsLinks().linkedin())
                                .github(data.snsLinks().github())
                                .behance(data.snsLinks().behance())
                                .build() : SnsLinks.empty())
                        .build())
                .toList();

        List<Member> saved = memberRepository.saveAll(members);

        log.info("운영진 일괄 생성 완료 - count={}", saved.size());
        return saved.stream().map(Member::getId).toList();
    }


    /**
     * 특정 기수의 모든 운영진 조회 (상세)
     */
    @Transactional(readOnly = true)
    public List<MemberDetailView> findByGeneration(Integer generationId) {
        log.debug("기수별 운영진 조회 - generationId={}", generationId);

        return memberRepository.findByGenerationIdOrderByRoleAscNameAsc(generationId)
                .stream()
                .map(MemberDetailView::from)
                .toList();
    }


}
