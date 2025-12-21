package sopt.org.homepage.recruitment.service.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.global.exception.ClientBadRequestException;
import sopt.org.homepage.recruitment.repository.query.RecruitPartIntroductionQueryRepository;
import sopt.org.homepage.recruitment.service.query.dto.RecruitPartIntroductionView;

/**
 * RecruitPartIntroductionQueryService
 * <p>
 * 책임: RecruitPartIntroduction 조회 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecruitPartIntroductionQueryService {

    private final RecruitPartIntroductionQueryRepository recruitPartIntroductionQueryRepository;

    /**
     * 특정 파트 소개 상세 조회
     */
    public RecruitPartIntroductionView getRecruitPartIntroductionDetail(Long id) {
        log.debug("Querying recruit part introduction detail: {}", id);

        return recruitPartIntroductionQueryRepository.findById(id)
                .map(RecruitPartIntroductionView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("RecruitPartIntroduction %d not found", id)
                ));
    }

    /**
     * 특정 기수의 모든 파트 소개 조회
     */
    public List<RecruitPartIntroductionView> getRecruitPartIntroductionsByGeneration(Integer generationId) {
        log.debug("Querying recruit part introductions for generation: {}", generationId);

        return recruitPartIntroductionQueryRepository.findByGenerationId(generationId)
                .stream()
                .map(RecruitPartIntroductionView::from)
                .toList();
    }

    /**
     * 특정 기수의 특정 파트 소개 조회
     */
    public RecruitPartIntroductionView getRecruitPartIntroductionByGenerationAndPart(
            Integer generationId, PartType part) {
        log.debug("Querying recruit part introduction {} for generation: {}", part, generationId);

        return recruitPartIntroductionQueryRepository.findByGenerationIdAndPart(generationId, part)
                .map(RecruitPartIntroductionView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("RecruitPartIntroduction for %s not found for generation %d",
                                part, generationId)
                ));
    }

    /**
     * 특정 기수의 파트 소개 개수 조회
     */
    public long countByGeneration(Integer generationId) {
        return recruitPartIntroductionQueryRepository.countByGenerationId(generationId);
    }

    /**
     * 특정 기수에 파트 소개 존재 여부 확인
     */
    public boolean existsByGeneration(Integer generationId) {
        return recruitPartIntroductionQueryRepository.existsByGenerationId(generationId);
    }

    /**
     * 여러 기수의 파트 소개 조회 (벌크 조회)
     */
    public List<RecruitPartIntroductionView> getRecruitPartIntroductionsByGenerations(
            List<Integer> generationIds) {
        log.debug("Querying recruit part introductions for generations: {}", generationIds);

        return recruitPartIntroductionQueryRepository.findByGenerationIdIn(generationIds)
                .stream()
                .map(RecruitPartIntroductionView::from)
                .toList();
    }
}
