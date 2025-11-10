package sopt.org.homepage.member.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.member.domain.Member;
import sopt.org.homepage.member.domain.vo.MemberRole;
import sopt.org.homepage.member.domain.vo.SnsLinks;
import sopt.org.homepage.member.repository.command.MemberCommandRepository;
import sopt.org.homepage.member.repository.query.MemberQueryRepository;
import sopt.org.homepage.member.service.command.dto.BulkCreateMembersCommand;
import sopt.org.homepage.member.service.command.dto.CreateMemberCommand;
import sopt.org.homepage.member.service.command.dto.UpdateMemberCommand;

import java.util.List;

/**
 * MemberCommandService
 *
 * 책임: Member 엔티티의 생성, 수정, 삭제 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberCommandService {

    private final MemberCommandRepository memberCommandRepository;
    private final MemberQueryRepository memberQueryRepository;

    /**
     * 운영진 생성
     */
    public Long createMember(CreateMemberCommand command) {
        log.info("Creating member for generation: {}", command.generationId());

        Member member = command.toEntity();
        Member saved = memberCommandRepository.save(member);

        log.info("Member created: {}", saved.getId());
        return saved.getId();
    }

    /**
     * 운영진 일괄 생성 (기존 데이터 모두 삭제 후 재생성)
     * Admin에서 기수별 운영진 전체 교체 시 사용
     */
    public List<Long> bulkCreateMembers(BulkCreateMembersCommand command) {
        log.info("Bulk creating members for generation: {}", command.generationId());

        // 1. 기존 운영진 모두 삭제
        memberCommandRepository.deleteByGenerationId(command.generationId());

        // 2. 새로운 운영진 생성
        List<Member> members = command.members().stream()
                .map(data -> Member.builder()
                        .generationId(command.generationId())
                        .role(MemberRole.fromLegacyRole(data.role()))  // 레거시 role 변환
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

        List<Member> saved = memberCommandRepository.saveAll(members);

        log.info("Bulk created {} members for generation: {}",
                saved.size(), command.generationId());

        return saved.stream().map(Member::getId).toList();
    }

    /**
     * 운영진 수정
     */
    public void updateMember(UpdateMemberCommand command) {
        log.info("Updating member: {}", command.id());

        Member member = memberQueryRepository.findById(command.id())
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Member %d not found", command.id())
                ));

        member.update(
                command.role(),
                command.name(),
                command.affiliation(),
                command.introduction(),
                command.profileImageUrl(),
                command.snsLinks() != null ? command.snsLinks().toVO() : SnsLinks.empty()
        );

        log.info("Member updated: {}", command.id());
    }

    /**
     * 프로필 이미지만 수정
     */
    public void updateProfileImage(Long memberId, String profileImageUrl) {
        log.info("Updating member profile image: {}", memberId);

        Member member = memberQueryRepository.findById(memberId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Member %d not found", memberId)
                ));

        member.updateProfileImage(profileImageUrl);

        log.info("Member profile image updated: {}", memberId);
    }

    /**
     * 소개글만 수정
     */
    public void updateIntroduction(Long memberId, String introduction) {
        log.info("Updating member introduction: {}", memberId);

        Member member = memberQueryRepository.findById(memberId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Member %d not found", memberId)
                ));

        member.updateIntroduction(introduction);

        log.info("Member introduction updated: {}", memberId);
    }

    /**
     * SNS 링크만 수정
     */
    public void updateSnsLinks(Long memberId, UpdateMemberCommand.SnsLinksCommand snsLinks) {
        log.info("Updating member SNS links: {}", memberId);

        Member member = memberQueryRepository.findById(memberId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Member %d not found", memberId)
                ));

        member.updateSnsLinks(snsLinks.toVO());

        log.info("Member SNS links updated: {}", memberId);
    }

    /**
     * 운영진 삭제
     */
    public void deleteMember(Long memberId) {
        log.info("Deleting member: {}", memberId);

        if (!memberQueryRepository.findById(memberId).isPresent()) {
            throw new ClientBadRequestException(
                    String.format("Member %d not found", memberId)
            );
        }

        memberCommandRepository.deleteById(memberId);

        log.info("Member deleted: {}", memberId);
    }

    /**
     * 특정 기수의 모든 운영진 삭제
     */
    public void deleteAllByGeneration(Integer generationId) {
        log.info("Deleting all members for generation: {}", generationId);

        memberCommandRepository.deleteByGenerationId(generationId);

        log.info("All members deleted for generation: {}", generationId);
    }
}