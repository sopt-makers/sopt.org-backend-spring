package sopt.org.homepage.generation.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.generation.domain.Generation;

/**
 * GenerationCommandRepository
 *
 * 책임: Generation 엔티티의 생성, 수정, 삭제
 * - Command 작업만 담당
 * - 복잡한 조회는 QueryRepository에서 처리
 */
public interface GenerationCommandRepository extends JpaRepository<Generation, Integer> {

    /**
     * 기수 ID로 존재 여부 확인
     */
    boolean existsById(Integer id);
}