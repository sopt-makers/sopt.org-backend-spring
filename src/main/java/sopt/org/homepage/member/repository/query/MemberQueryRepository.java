package sopt.org.homepage.member.repository.query;

import sopt.org.homepage.member.domain.Member;
import sopt.org.homepage.member.domain.vo.MemberRole;

import java.util.List;
import java.util.Optional;

/**
 * MemberQueryRepository
 *
 * 책임: Member 조회 쿼리
 */
public interface MemberQueryRepository {

    /**
     * ID로 조회
     */
    Optional<Member> findById(Long id);

    /**
     * 특정 기수의 모든 운영진 조회
     */
    List<Member> findByGenerationId(Integer generationId);

    /**
     * 특정 기수의 특정 역할 운영진 조회
     */
    List<Member> findByGenerationIdAndRole(Integer generationId, MemberRole role);

    /**
     * 특정 기수의 운영진 수 조회
     */
    long countByGenerationId(Integer generationId);

    /**
     * 특정 기수에 운영진 존재 여부
     */
    boolean existsByGenerationId(Integer generationId);

    /**
     * 여러 기수의 운영진 조회 (벌크 조회)
     */
    List<Member> findByGenerationIdIn(List<Integer> generationIds);

    /**
     * 특정 기수의 회장 조회
     */
    Optional<Member> findPresidentByGenerationId(Integer generationId);
}