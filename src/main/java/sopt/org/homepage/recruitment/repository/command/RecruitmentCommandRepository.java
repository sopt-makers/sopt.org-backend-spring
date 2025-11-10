package sopt.org.homepage.recruitment.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.recruitment.domain.Recruitment;

/**
 * RecruitmentCommandRepository
 *
 * 책임: Recruitment 엔티티의 생성, 수정, 삭제
 */
public interface RecruitmentCommandRepository extends JpaRepository<Recruitment, Long> {

    /**
     * 특정 기수의 모든 모집 일정 삭제
     */
    void deleteByGenerationId(Integer generationId);
}