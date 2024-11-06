package sopt.org.homepage.project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.org.homepage.internal.playground.PlaygroundService;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final PlaygroundService playgroundService;


}
