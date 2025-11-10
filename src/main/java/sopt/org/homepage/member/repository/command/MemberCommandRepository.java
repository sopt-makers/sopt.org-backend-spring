package sopt.org.homepage.member.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.member.domain.Member;

import java.util.List;

/**
 * MemberCommandRepository
 *
 * 책임: Member 엔티티의 생성, 수정, 삭제
 */
public interface MemberCommandRepository extends JpaRepository<Member, Long> {

    /**
     * 특정 기수의 모든 운영진 삭제
     */
    void deleteByGenerationId(Integer generationId);

    /**
     * 특정 운영진 ID 목록으로 삭제
     */
    void deleteByIdIn(List<Long> ids);
}