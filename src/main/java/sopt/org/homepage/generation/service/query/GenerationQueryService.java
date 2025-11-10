package sopt.org.homepage.generation.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.generation.domain.Generation;
import sopt.org.homepage.generation.repository.query.GenerationQueryRepository;
import sopt.org.homepage.generation.service.query.dto.GenerationDetailView;
import sopt.org.homepage.generation.service.query.dto.GenerationListView;

import java.util.List;

/**
 * GenerationQueryService
 *
 * 책임: Generation 조회 처리
 * - Query 작업만 담당
 * - 읽기 전용 트랜잭션
 * - 캐시 활용 가능
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GenerationQueryService {

    private final GenerationQueryRepository generationQueryRepository;

    /**
     * 특정 기수 상세 조회
     *
     * @param generationId 기수 ID
     * @return 기수 상세 정보
     * @throws ClientBadRequestException 기수가 존재하지 않는 경우
     */
    public GenerationDetailView getGenerationDetail(Integer generationId) {
        log.debug("Querying generation detail: {}", generationId);

        Generation generation = generationQueryRepository.findById(generationId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Generation %d not found", generationId)
                ));

        return GenerationDetailView.from(generation);
    }

    /**
     * 최신 기수 상세 조회
     *
     * @return 최신 기수 상세 정보
     * @throws ClientBadRequestException 기수가 존재하지 않는 경우
     */
    public GenerationDetailView getLatestGeneration() {
        log.debug("Querying latest generation");

        Generation generation = generationQueryRepository.findLatest()
                .orElseThrow(() -> new ClientBadRequestException("No generation found"));

        return GenerationDetailView.from(generation);
    }

    /**
     * 최신 기수 ID 조회
     *
     * @return 최신 기수 ID
     */
    public Integer getLatestGenerationId() {
        log.debug("Querying latest generation id");

        return generationQueryRepository.findLatest()
                .map(Generation::getId)
                .orElseThrow(() -> new ClientBadRequestException("No generation found"));
    }

    /**
     * 모든 기수 목록 조회 (최신순)
     *
     * @return 기수 목록 (간략 정보)
     */
    public List<GenerationListView> getAllGenerations() {
        log.debug("Querying all generations");

        return generationQueryRepository.findAllOrderByIdDesc().stream()
                .map(GenerationListView::from)
                .toList();
    }

    /**
     * 특정 범위의 기수 목록 조회
     *
     * @param startId 시작 기수
     * @param endId 종료 기수
     * @return 기수 목록
     */
    public List<GenerationListView> getGenerationsByRange(Integer startId, Integer endId) {
        log.debug("Querying generations from {} to {}", startId, endId);

        return generationQueryRepository.findByIdBetween(startId, endId).stream()
                .map(GenerationListView::from)
                .toList();
    }

    /**
     * 기수 존재 여부 확인
     */
    public boolean existsGeneration(Integer generationId) {
        return generationQueryRepository.existsById(generationId);
    }
}