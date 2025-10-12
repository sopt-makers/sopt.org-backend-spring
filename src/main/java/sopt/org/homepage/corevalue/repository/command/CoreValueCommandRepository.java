package sopt.org.homepage.corevalue.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.corevalue.domain.CoreValue;

import java.util.List;

/**
 * CoreValueCommandRepository
 *
 * 책임: CoreValue 엔티티의 생성, 수정, 삭제
 */
public interface CoreValueCommandRepository extends JpaRepository<CoreValue, Long> {

    /**
     * 특정 기수의 모든 핵심 가치 삭제
     */
    void deleteByGenerationId(Integer generationId);

    /**
     * 특정 기수의 핵심 가치 ID 목록으로 삭제
     */
    void deleteByIdIn(List<Long> ids);
}