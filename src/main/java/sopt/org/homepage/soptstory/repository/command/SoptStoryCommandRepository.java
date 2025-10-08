package sopt.org.homepage.soptstory.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.soptstory.domain.SoptStory;

import java.util.Optional;

/**
 * SoptStory Command Repository
 *
 * 책임:
 * - SoptStory 생성, 수정, 삭제
 * - 단순 ID 조회 (Command용)
 */
@Repository
public interface SoptStoryCommandRepository extends JpaRepository<SoptStory, Long> {

    /**
     * ID로 SoptStory 조회 (Command 전용 - 수정/삭제용)
     *
     * @param id SoptStory ID
     * @return SoptStory Optional
     */
    Optional<SoptStory> findById(Long id);

    /**
     * URL 중복 체크
     *
     * @param url SoptStory URL (Value Object의 value)
     * @return 존재 여부
     */
    boolean existsByUrl_Value(String url);
}