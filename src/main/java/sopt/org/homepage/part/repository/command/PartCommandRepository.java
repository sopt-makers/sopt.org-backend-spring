package sopt.org.homepage.part.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.part.domain.Part;

/**
 * PartCommandRepository
 *
 * 책임: PartType 엔티티의 생성, 수정, 삭제
 */
public interface PartCommandRepository extends JpaRepository<Part, Long> {

    /**
     * 특정 기수의 모든 파트 삭제
     */
    void deleteByGenerationId(Integer generationId);
}