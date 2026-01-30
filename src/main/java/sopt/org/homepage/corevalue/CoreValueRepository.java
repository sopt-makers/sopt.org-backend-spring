package sopt.org.homepage.corevalue;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * CoreValueRepository
 * <p>
 * 통합 Repository (Command + Query) - QueryDSL 구현체를 JpaRepository 메서드로 대체
 */
public interface CoreValueRepository extends JpaRepository<CoreValue, Long> {

    /**
     * 특정 기수의 모든 핵심 가치 조회 (순서대로)
     */
    List<CoreValue> findByGenerationIdOrderByDisplayOrderAsc(Integer generationId);

    /**
     * 특정 기수의 모든 핵심 가치 삭제
     */
    void deleteByGenerationId(Integer generationId);

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
    @Query("SELECT cv FROM CoreValue cv WHERE cv.generationId IN :generationIds ORDER BY cv.generationId DESC, cv.displayOrder ASC")
    List<CoreValue> findByGenerationIdIn(@Param("generationIds") List<Integer> generationIds);

    /**
     * 특정 기수의 핵심 가치 ID 목록으로 삭제
     */
    void deleteByIdIn(List<Long> ids);
}
