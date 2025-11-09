package sopt.org.homepage.generation.repository.query;

import sopt.org.homepage.generation.domain.Generation;

import java.util.List;
import java.util.Optional;

/**
 * GenerationQueryRepository
 *
 * 책임: Generation 조회 쿼리
 * - 복잡한 조회 로직은 구현체(QueryDSL)에서 처리
 * - Command와 분리하여 읽기 성능 최적화
 */
public interface GenerationQueryRepository {

    /**
     * 기수 ID로 조회
     */
    Optional<Generation> findById(Integer id);

    /**
     * 최신 기수 조회
     */
    Optional<Generation> findLatest();

    /**
     * 모든 기수 조회 (최신순)
     */
    List<Generation> findAllOrderByIdDesc();

    /**
     * 특정 기수 범위 조회
     *
     * @param startId 시작 기수 (inclusive)
     * @param endId 종료 기수 (inclusive)
     */
    List<Generation> findByIdBetween(Integer startId, Integer endId);

    /**
     * 기수 존재 여부 확인
     */
    boolean existsById(Integer id);
}