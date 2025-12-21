package sopt.org.homepage.recruitment.repository.query;

import java.util.List;
import java.util.Optional;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.recruitment.domain.RecruitPartIntroduction;

/**
 * RecruitPartIntroductionQueryRepository
 * <p>
 * 책임: RecruitPartIntroduction 조회 쿼리
 */
public interface RecruitPartIntroductionQueryRepository {

    /**
     * ID로 조회
     */
    Optional<RecruitPartIntroduction> findById(Long id);

    /**
     * 특정 기수의 모든 파트 소개 조회
     */
    List<RecruitPartIntroduction> findByGenerationId(Integer generationId);

    /**
     * 특정 기수의 특정 파트 소개 조회
     */
    Optional<RecruitPartIntroduction> findByGenerationIdAndPart(Integer generationId, PartType part);

    /**
     * 특정 기수의 파트 소개 개수
     */
    long countByGenerationId(Integer generationId);

    /**
     * 특정 기수에 파트 소개 존재 여부
     */
    boolean existsByGenerationId(Integer generationId);

    /**
     * 여러 기수의 파트 소개 조회 (벌크 조회)
     */
    List<RecruitPartIntroduction> findByGenerationIdIn(List<Integer> generationIds);
}
