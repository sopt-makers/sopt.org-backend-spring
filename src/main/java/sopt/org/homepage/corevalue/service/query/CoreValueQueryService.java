package sopt.org.homepage.corevalue.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.corevalue.repository.query.CoreValueQueryRepository;
import sopt.org.homepage.corevalue.service.query.dto.CoreValueView;
import sopt.org.homepage.exception.ClientBadRequestException;

import java.util.List;

/**
 * CoreValueQueryService
 *
 * 책임: CoreValue 조회 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CoreValueQueryService {

    private final CoreValueQueryRepository coreValueQueryRepository;

    /**
     * 특정 기수의 모든 핵심 가치 조회 (순서대로)
     */
    public List<CoreValueView> getCoreValuesByGeneration(Integer generationId) {
        log.debug("Querying core values for generation: {}", generationId);

        return coreValueQueryRepository
                .findByGenerationIdOrderByDisplayOrder(generationId)
                .stream()
                .map(CoreValueView::from)
                .toList();
    }

    /**
     * 특정 핵심 가치 조회
     */
    public CoreValueView getCoreValue(Long coreValueId) {
        log.debug("Querying core value: {}", coreValueId);

        return coreValueQueryRepository.findById(coreValueId)
                .map(CoreValueView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("CoreValue %d not found", coreValueId)
                ));
    }

    /**
     * 특정 기수의 핵심 가치 개수 조회
     */
    public long countByGeneration(Integer generationId) {
        return coreValueQueryRepository.countByGenerationId(generationId);
    }

    /**
     * 여러 기수의 핵심 가치 조회 (벌크 조회)
     */
    public List<CoreValueView> getCoreValuesByGenerations(List<Integer> generationIds) {
        log.debug("Querying core values for generations: {}", generationIds);

        return coreValueQueryRepository
                .findByGenerationIdIn(generationIds)
                .stream()
                .map(CoreValueView::from)
                .toList();
    }
}