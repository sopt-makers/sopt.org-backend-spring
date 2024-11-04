package sopt.org.homepage.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.admin.entity.MainEntity;

public interface MainRepository extends JpaRepository<MainEntity, Integer> {
    MainEntity findByGeneration(int generation);
}
