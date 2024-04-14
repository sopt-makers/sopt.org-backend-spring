package sopt.org.homepage.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.internal.playground.PlaygroundService;
import sopt.org.homepage.semester.dto.SemesterDao;
import sopt.org.homepage.semester.repo.SemestersQueryRepository;
import sopt.org.homepage.semester.repo.SemestersRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final PlaygroundService playgroundService;


}
