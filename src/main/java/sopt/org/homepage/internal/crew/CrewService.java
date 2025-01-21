package sopt.org.homepage.internal.crew;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.internal.crew.dto.StudyResponse;
import sopt.org.homepage.common.mapper.ResponseMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.net.URLEncoder;



@Slf4j
@RequiredArgsConstructor
@Service
public class CrewService {
    private final CrewClient crewClient;
    private final ResponseMapper responseMapper;
    private final AuthConfig authConfig;
    private final String studyCategory = URLEncoder.encode("스터디", StandardCharsets.UTF_8);

    public List<StudyResponse> getAllStudy(Integer generation, boolean isActive){
        val MAX_TAKE_COUNT = 50;

        val crewApiResponse = crewClient.getAllStudy(1, MAX_TAKE_COUNT, generation, isActive, studyCategory);

//        val crewApiResponse = crewClient.getAllStudy(authConfig.getCrewApiToken(),1, MAX_TAKE_COUNT, activeGen, active, studyCategory);

        return crewApiResponse.data().meetings().stream().map(responseMapper::toStudyResponse).toList();
    }

    public Integer getStudyCount(Integer generation, boolean isActive) {
        val crewApiResponse = crewClient.getAllStudy(1, 1, generation, isActive, studyCategory);

        return crewApiResponse.data().meta().itemCount();
//        try {
//            val activeGen = (generation != null) ? generation : PREV_GEN;
//            val active = !activeGen.equals(PREV_GEN);
//
//            val crewApiResponse = crewClient.getAllStudy(authConfig.getCrewApiToken(), 1, 1, activeGen, active, studyCategory);
//
//            if (crewApiResponse == null || crewApiResponse.data() == null || crewApiResponse.data().meta() == null) {
//                return 0; // 또는 다른 기본값
//            }
//
//            return crewApiResponse.data().meta().itemCount();
//        } catch (Exception e) {
//            log.error("Failed to get study count", e);
//            return 0; // 또는 다른 기본값
//        }

    }


}
