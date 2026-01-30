package sopt.org.homepage.infrastructure.external.crew;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrewService {
    private final CrewClient crewClient;

    public Integer getStudyCount(Integer generation) {
        try {
            val response = crewClient.getStudyCount(generation);
            return response.meetingCount();
        } catch (Exception e) {
            log.error("Failed to get study count for generation {}", generation, e);
            return 0;
        }
    }
}
