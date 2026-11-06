package sopt.org.homepage.news.instagram;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

public interface InstagramCollectionRepository extends JpaRepository<InstagramCollectionState, Integer> {
    @Modifying
    @Query(value = "insert into instagram_collection_state (id, item_offset, failures, had_post_failure) values (1, 0, 0, false) on conflict (id) do nothing", nativeQuery = true)
    void initialize();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from InstagramCollectionState s where s.id = 1")
    InstagramCollectionState lockState();
}
