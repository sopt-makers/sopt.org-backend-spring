package sopt.org.homepage.recruitment.service.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.global.exception.ClientBadRequestException;
import sopt.org.homepage.recruitment.domain.vo.RecruitType;
import sopt.org.homepage.recruitment.repository.query.RecruitmentQueryRepository;
import sopt.org.homepage.recruitment.service.query.dto.RecruitmentView;

/**
 * RecruitmentQueryService
 * <p>
 * 책임: Recruitment 조회 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecruitmentQueryService {

    private final RecruitmentQueryRepository recruitmentQueryRepository;

    /**
     * 특정 모집 일정 상세 조회
     */
    public RecruitmentView getRecruitmentDetail(Long recruitmentId) {
        log.debug("Querying recruitment detail: {}", recruitmentId);

        return recruitmentQueryRepository.findById(recruitmentId)
                .map(RecruitmentView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Recruitment %d not found", recruitmentId)
                ));
    }

    /**
     * 특정 기수의 모든 모집 일정 조회
     */
    public List<RecruitmentView> getRecruitmentsByGeneration(Integer generationId) {
        log.debug("Querying recruitments for generation: {}", generationId);

        return recruitmentQueryRepository.findByGenerationId(generationId)
                .stream()
                .map(RecruitmentView::from)
                .toList();
    }

    /**
     * 특정 기수의 특정 타입 모집 일정 조회
     */
    public RecruitmentView getRecruitmentByGenerationAndType(Integer generationId, RecruitType recruitType) {
        log.debug("Querying recruitment {} for generation: {}", recruitType, generationId);

        return recruitmentQueryRepository.findByGenerationIdAndRecruitType(generationId, recruitType)
                .map(RecruitmentView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Recruitment %s not found for generation %d", recruitType, generationId)
                ));
    }

    /**
     * 특정 기수의 모집 일정 개수 조회
     */
    public long countByGeneration(Integer generationId) {
        return recruitmentQueryRepository.countByGenerationId(generationId);
    }

    /**
     * 특정 기수에 모집 일정 존재 여부 확인
     */
    public boolean existsByGeneration(Integer generationId) {
        return recruitmentQueryRepository.existsByGenerationId(generationId);
    }

    /**
     * 여러 기수의 모집 일정 조회 (벌크 조회)
     */
    public List<RecruitmentView> getRecruitmentsByGenerations(List<Integer> generationIds) {
        log.debug("Querying recruitments for generations: {}", generationIds);

        return recruitmentQueryRepository.findByGenerationIdIn(generationIds)
                .stream()
                .map(RecruitmentView::from)
                .toList();
    }

    /**
     * 현재 모집 중인 모집 일정 조회
     */
    public List<RecruitmentView> getActiveRecruitments() {
        log.debug("Querying active recruitments");

        return recruitmentQueryRepository.findActiveRecruitments()
                .stream()
                .map(RecruitmentView::from)
                .toList();
    }
}
