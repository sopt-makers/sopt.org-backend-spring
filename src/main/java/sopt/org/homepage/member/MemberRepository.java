package sopt.org.homepage.member;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sopt.org.homepage.member.vo.MemberRole;

/**
 * MemberRepository
 * <p>
 * 통합 Repository (Command + Query) - SOPT 운영진 관리
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 특정 기수의 모든 운영진 조회 (정렬 없음 - Service에서 정렬)
     */
    List<Member> findByGenerationId(Integer generationId);

    /**
     * 특정 기수의 모든 운영진 조회 (역할순, 이름순)
     *
     * @deprecated 알파벳순 정렬 이슈로 사용 지양. findByGenerationId + 애플리케이션 정렬 사용 권장
     */
    @Deprecated
    List<Member> findByGenerationIdOrderByRoleAscNameAsc(Integer generationId);

    /**
     * 특정 기수의 특정 역할 운영진 조회
     */
    List<Member> findByGenerationIdAndRoleOrderByNameAsc(Integer generationId, MemberRole role);

    /**
     * 특정 기수의 모든 운영진 삭제
     */
    void deleteByGenerationId(Integer generationId);

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
    @Query("SELECT m FROM Member m WHERE m.generationId IN :generationIds ORDER BY m.generationId DESC, m.role ASC, m.name ASC")
    List<Member> findByGenerationIdIn(@Param("generationIds") List<Integer> generationIds);

    /**
     * 특정 기수의 회장 조회
     */
    Optional<Member> findByGenerationIdAndRole(Integer generationId, MemberRole role);
}
