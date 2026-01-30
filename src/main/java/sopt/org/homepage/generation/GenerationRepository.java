package sopt.org.homepage.generation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * GenerationRepository
 * <p>
 * 통합 Repository (Command + Query) - PK는 Integer (기수 번호: 35, 36, ...)
 */
public interface GenerationRepository extends JpaRepository<Generation, Integer> {

    /**
     * 최신 기수 조회 (가장 높은 번호)
     */
    Optional<Generation> findFirstByOrderByIdDesc();

    /**
     * 모든 기수 조회 (최신순)
     */
    List<Generation> findAllByOrderByIdDesc();


    boolean existsById(Integer id);


}
