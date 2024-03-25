package sopt.org.homepage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.entity.SemesterEntity;

public interface SemestersRepository extends JpaRepository<SemesterEntity, Long> {
}
