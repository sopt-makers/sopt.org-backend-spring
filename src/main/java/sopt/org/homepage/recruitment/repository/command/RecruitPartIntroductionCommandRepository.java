package sopt.org.homepage.recruitment.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.recruitment.domain.RecruitPartIntroduction;

/**
 * RecruitPartIntroductionCommandRepository
 *
 * 책임: RecruitPartIntroduction 엔티티의 생성, 수정, 삭제
 */
public interface RecruitPartIntroductionCommandRepository extends JpaRepository<RecruitPartIntroduction, Long> {

    /**
     * 특정 기수의 모든 파트 소개 삭제
     */
    void deleteByGenerationId(Integer generationId);
}