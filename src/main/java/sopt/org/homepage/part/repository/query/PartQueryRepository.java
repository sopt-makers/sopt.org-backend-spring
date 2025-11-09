package sopt.org.homepage.part.repository.query;

import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.part.domain.Part;

import java.util.List;
import java.util.Optional;

/**
 * PartQueryRepository
 *
 * 책임: PartType 조회 쿼리
 */
public interface PartQueryRepository {

    /**
     * ID로 조회
     */
    Optional<Part> findById(Long id);

    /**
     * 특정 기수의 모든 파트 조회
     */
    List<Part> findByGenerationId(Integer generationId);

    /**
     * 특정 기수의 특정 파트 조회
     */
    Optional<Part> findByGenerationIdAndPartType(Integer generationId, PartType partType);

    /**
     * 특정 기수의 파트 개수 조회
     */
    long countByGenerationId(Integer generationId);

    /**
     * 특정 기수에 파트 존재 여부
     */
    boolean existsByGenerationId(Integer generationId);

    /**
     * 여러 기수의 파트 조회 (벌크 조회)
     */
    List<Part> findByGenerationIdIn(List<Integer> generationIds);
}