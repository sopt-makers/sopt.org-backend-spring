package sopt.org.homepage.corevalue.repository.query;

import sopt.org.homepage.corevalue.domain.CoreValue;

import java.util.List;
import java.util.Optional;

/**
 * CoreValueQueryRepository
 *
 * 책임: CoreValue 조회 쿼리
 */
public interface CoreValueQueryRepository {

    /**
     * ID로 조회
     */
    Optional<CoreValue> findById(Long id);

    /**
     * 특정 기수의 모든 핵심 가치 조회 (순서대로)
     */
    List<CoreValue> findByGenerationIdOrderByDisplayOrder(Integer generationId);

    /**
     * 특정 기수의 핵심 가치 개수 조회
     */
    long countByGenerationId(Integer generationId);

    /**
     * 특정 기수에 핵심 가치 존재 여부
     */
    boolean existsByGenerationId(Integer generationId);

    /**
     * 여러 기수의 핵심 가치 조회 (벌크 조회)
     */
    List<CoreValue> findByGenerationIdIn(List<Integer> generationIds);
}