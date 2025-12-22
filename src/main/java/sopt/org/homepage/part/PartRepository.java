package sopt.org.homepage.part;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sopt.org.homepage.global.common.type.PartType;

/**
 * PartRepository
 * <p>
 * 통합 Repository (Command + Query) - SOPT 파트 관리 (ANDROID, IOS, WEB, SERVER, PLAN, DESIGN)
 */
public interface PartRepository extends JpaRepository<Part, Long> {

    /**
     * 특정 기수의 모든 파트 조회 (파트 타입 순서대로)
     */
    List<Part> findByGenerationIdOrderByPartTypeAsc(Integer generationId);

    /**
     * 특정 기수의 특정 파트 조회
     */
    Optional<Part> findByGenerationIdAndPartType(Integer generationId, PartType partType);

    /**
     * 특정 기수의 모든 파트 삭제
     */
    void deleteByGenerationId(Integer generationId);

    /**
     * 특정 기수의 파트 개수 조회
     */
    long countByGenerationId(Integer generationId);

    /**
     * 특정 기수에 파트 존재 여부
     */
    boolean existsByGenerationId(Integer generationId);

    /**
     * 특정 기수에 특정 파트 존재 여부
     */
    boolean existsByGenerationIdAndPartType(Integer generationId, PartType partType);

    /**
     * 여러 기수의 파트 조회 (벌크 조회)
     */
    @Query("SELECT p FROM Part p WHERE p.generationId IN :generationIds ORDER BY p.generationId DESC, p.partType ASC")
    List<Part> findByGenerationIdIn(@Param("generationIds") List<Integer> generationIds);
}
