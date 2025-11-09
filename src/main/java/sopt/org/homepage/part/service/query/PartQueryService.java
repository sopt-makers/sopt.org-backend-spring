package sopt.org.homepage.part.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.part.repository.query.PartQueryRepository;
import sopt.org.homepage.part.service.query.dto.PartCurriculumView;
import sopt.org.homepage.part.service.query.dto.PartDetailView;
import sopt.org.homepage.part.service.query.dto.PartIntroductionView;

import java.util.List;

/**
 * PartQueryService
 *
 * 책임: PartType 조회 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PartQueryService {

    private final PartQueryRepository partQueryRepository;

    /**
     * 특정 파트 상세 조회
     */
    public PartDetailView getPartDetail(Long partId) {
        log.debug("Querying partType detail: {}", partId);

        return partQueryRepository.findById(partId)
                .map(PartDetailView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("PartType %d not found", partId)
                ));
    }

    /**
     * 특정 기수의 모든 파트 조회 (상세 정보)
     */
    public List<PartDetailView> getPartsByGeneration(Integer generationId) {
        log.debug("Querying parts for generation: {}", generationId);

        return partQueryRepository.findByGenerationId(generationId)
                .stream()
                .map(PartDetailView::from)
                .toList();
    }

    /**
     * 특정 기수의 특정 파트 조회
     */
    public PartDetailView getPartByGenerationAndType(Integer generationId, PartType partType) {
        log.debug("Querying partType {} for generation: {}", partType, generationId);

        return partQueryRepository.findByGenerationIdAndPartType(generationId, partType)
                .map(PartDetailView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("PartType %s not found for generation %d", partType, generationId)
                ));
    }

    /**
     * 특정 기수의 모든 파트 소개 조회 (Main 페이지용)
     */
    public List<PartIntroductionView> getPartIntroductionsByGeneration(Integer generationId) {
        log.debug("Querying partType introductions for generation: {}", generationId);

        return partQueryRepository.findByGenerationId(generationId)
                .stream()
                .map(PartIntroductionView::from)
                .toList();
    }

    /**
     * 특정 기수의 모든 파트 커리큘럼 조회 (About 페이지용)
     */
    public List<PartCurriculumView> getPartCurriculumsByGeneration(Integer generationId) {
        log.debug("Querying partType curriculums for generation: {}", generationId);

        return partQueryRepository.findByGenerationId(generationId)
                .stream()
                .map(PartCurriculumView::from)
                .toList();
    }

    /**
     * 특정 기수의 파트 개수 조회
     */
    public long countByGeneration(Integer generationId) {
        return partQueryRepository.countByGenerationId(generationId);
    }

    /**
     * 특정 기수에 파트 존재 여부 확인
     */
    public boolean existsByGeneration(Integer generationId) {
        return partQueryRepository.existsByGenerationId(generationId);
    }

    /**
     * 여러 기수의 파트 조회 (벌크 조회)
     */
    public List<PartDetailView> getPartsByGenerations(List<Integer> generationIds) {
        log.debug("Querying parts for generations: {}", generationIds);

        return partQueryRepository.findByGenerationIdIn(generationIds)
                .stream()
                .map(PartDetailView::from)
                .toList();
    }
}