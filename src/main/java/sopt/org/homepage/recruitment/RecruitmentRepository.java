package sopt.org.homepage.recruitment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sopt.org.homepage.recruitment.vo.RecruitType;

/**
 * RecruitmentRepository
 * <p>
 * 통합 Repository (Command + Query) - 기수별 모집 일정 관리 (OB/YB)
 */
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    /**
     * 특정 기수의 모든 모집 일정 조회 (OB, YB 순)
     */
    List<Recruitment> findByGenerationIdOrderByRecruitTypeAsc(Integer generationId);

    /**
     * 특정 기수의 특정 타입 모집 일정 조회
     */
    Optional<Recruitment> findByGenerationIdAndRecruitType(Integer generationId, RecruitType recruitType);

    /**
     * 특정 기수의 모든 모집 일정 삭제
     */
    void deleteByGenerationId(Integer generationId);

    /**
     * 특정 기수의 모집 일정 수 조회
     */
    long countByGenerationId(Integer generationId);

    /**
     * 특정 기수에 모집 일정 존재 여부
     */
    boolean existsByGenerationId(Integer generationId);


    /**
     * 여러 기수의 모집 일정 조회 (벌크 조회)
     */
    @Query("SELECT r FROM Recruitment r WHERE r.generationId IN :generationIds ORDER BY r.generationId DESC, r.recruitType ASC")
    List<Recruitment> findByGenerationIdIn(@Param("generationIds") List<Integer> generationIds);

    /**
     * 현재 모집 중인 모집 일정 조회
     */
    @Query("SELECT r FROM Recruitment r WHERE r.schedule.applicationStartTime <= :now AND r.schedule.finalResultTime >= :now ORDER BY r.generationId DESC")
    List<Recruitment> findActiveRecruitments(@Param("now") String now);
}
