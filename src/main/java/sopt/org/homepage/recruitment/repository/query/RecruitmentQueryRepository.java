package sopt.org.homepage.recruitment.repository.query;

import sopt.org.homepage.recruitment.domain.Recruitment;
import sopt.org.homepage.recruitment.domain.vo.RecruitType;

import java.util.List;
import java.util.Optional;

/**
 * RecruitmentQueryRepository
 *
 * 책임: Recruitment 조회 쿼리
 */
public interface RecruitmentQueryRepository {

    /**
     * ID로 조회
     */
    Optional<Recruitment> findById(Long id);

    /**
     * 특정 기수의 모든 모집 일정 조회
     */
    List<Recruitment> findByGenerationId(Integer generationId);

    /**
     * 특정 기수의 특정 타입 모집 일정 조회 (OB/YB)
     */
    Optional<Recruitment> findByGenerationIdAndRecruitType(Integer generationId, RecruitType recruitType);

    /**
     * 특정 기수의 모집 일정 개수
     */
    long countByGenerationId(Integer generationId);

    /**
     * 특정 기수에 모집 일정 존재 여부
     */
    boolean existsByGenerationId(Integer generationId);

    /**
     * 여러 기수의 모집 일정 조회 (벌크 조회)
     */
    List<Recruitment> findByGenerationIdIn(List<Integer> generationIds);

    /**
     * 현재 모집 중인 기수의 모집 일정 조회
     */
    List<Recruitment> findActiveRecruitments();
}