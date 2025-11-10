package sopt.org.homepage.member.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.member.domain.vo.MemberRole;
import sopt.org.homepage.member.repository.query.MemberQueryRepository;
import sopt.org.homepage.member.service.query.dto.MemberDetailView;
import sopt.org.homepage.member.service.query.dto.MemberListView;

import java.util.List;

/**
 * MemberQueryService
 *
 * 책임: Member 조회 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberQueryRepository memberQueryRepository;

    /**
     * 특정 운영진 상세 조회
     */
    public MemberDetailView getMemberDetail(Long memberId) {
        log.debug("Querying member detail: {}", memberId);

        return memberQueryRepository.findById(memberId)
                .map(MemberDetailView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Member %d not found", memberId)
                ));
    }

    /**
     * 특정 기수의 모든 운영진 조회
     */
    public List<MemberDetailView> getMembersByGeneration(Integer generationId) {
        log.debug("Querying members for generation: {}", generationId);

        return memberQueryRepository.findByGenerationId(generationId)
                .stream()
                .map(MemberDetailView::from)
                .toList();
    }

    /**
     * 특정 기수의 모든 운영진 조회 (간략 정보)
     */
    public List<MemberListView> getMemberListByGeneration(Integer generationId) {
        log.debug("Querying member list for generation: {}", generationId);

        return memberQueryRepository.findByGenerationId(generationId)
                .stream()
                .map(MemberListView::from)
                .toList();
    }

    /**
     * 특정 기수의 특정 역할 운영진 조회
     */
    public List<MemberDetailView> getMembersByRole(Integer generationId, MemberRole role) {
        log.debug("Querying members for generation: {} with role: {}", generationId, role);

        return memberQueryRepository.findByGenerationIdAndRole(generationId, role)
                .stream()
                .map(MemberDetailView::from)
                .toList();
    }

    /**
     * 특정 기수의 회장 조회
     */
    public MemberDetailView getPresidentByGeneration(Integer generationId) {
        log.debug("Querying president for generation: {}", generationId);

        return memberQueryRepository.findPresidentByGenerationId(generationId)
                .map(MemberDetailView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("President not found for generation %d", generationId)
                ));
    }

    /**
     * 특정 기수의 운영진 수 조회
     */
    public long countByGeneration(Integer generationId) {
        return memberQueryRepository.countByGenerationId(generationId);
    }

    /**
     * 여러 기수의 운영진 조회 (벌크 조회)
     */
    public List<MemberDetailView> getMembersByGenerations(List<Integer> generationIds) {
        log.debug("Querying members for generations: {}", generationIds);

        return memberQueryRepository.findByGenerationIdIn(generationIds)
                .stream()
                .map(MemberDetailView::from)
                .toList();
    }

    /**
     * 특정 기수에 운영진 존재 여부 확인
     */
    public boolean existsByGeneration(Integer generationId) {
        return memberQueryRepository.existsByGenerationId(generationId);
    }
}