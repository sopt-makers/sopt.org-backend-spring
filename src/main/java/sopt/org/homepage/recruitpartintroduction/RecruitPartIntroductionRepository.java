package sopt.org.homepage.recruitpartintroduction;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sopt.org.homepage.global.common.type.PartType;

/**
 * RecruitPartIntroductionRepository
 * <p>
 * 통합 Repository (Command + Query) - 모집 시 파트별 소개 관리
 */
public interface RecruitPartIntroductionRepository extends JpaRepository<RecruitPartIntroduction, Long> {

    /**
     * 특정 기수의 모든 파트 소개 조회 (파트 순서대로)
     */
    List<RecruitPartIntroduction> findByGenerationIdOrderByPartAsc(Integer generationId);

    /**
     * 특정 기수의 특정 파트 소개 조회
     */
    Optional<RecruitPartIntroduction> findByGenerationIdAndPart(Integer generationId, PartType part);

    /**
     * 특정 기수의 모든 파트 소개 삭제
     */
    void deleteByGenerationId(Integer generationId);

    /**
     * 특정 기수의 파트 소개 수 조회
     */
    long countByGenerationId(Integer generationId);

    /**
     * 특정 기수에 파트 소개 존재 여부
     */
    boolean existsByGenerationId(Integer generationId);

    /**
     * 특정 기수에 특정 파트 존재 여부
     */
    boolean existsByGenerationIdAndPart(Integer generationId, PartType part);

    /**
     * 여러 기수의 파트 소개 조회 (벌크 조회)
     */
    @Query("SELECT r FROM RecruitPartIntroduction r WHERE r.generationId IN :generationIds ORDER BY r.generationId DESC, r.part ASC")
    List<RecruitPartIntroduction> findByGenerationIdIn(@Param("generationIds") List<Integer> generationIds);
}
