package sopt.org.homepage.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.main.entity.MainEntity;

public interface MainRepository extends JpaRepository<MainEntity, Integer> {
    MainEntity findByGeneration(int generation);
    MainEntity findFirstByOrderByGenerationDesc();
}
