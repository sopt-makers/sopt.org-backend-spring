package sopt.org.homepage.internal.crew;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import sopt.org.homepage.internal.crew.dto.StudyResponse;
import sopt.org.homepage.mapper.ResponseMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.net.URLEncoder;



@Slf4j
@RequiredArgsConstructor
@Service
public class CrewService {
    private final CrewClient crewClient;
    private final ResponseMapper responseMapper;

    public List<StudyResponse> getAllStudy(Integer generation){
        val MAX_TAKE_COUNT = 50;
        val PREV_GEN = 33;
        val activeGen = (generation != null) ? generation : PREV_GEN;
        val active = activeGen != PREV_GEN;

        val studyCategory = URLEncoder.encode("스터디", StandardCharsets.UTF_8);

        val crewApiResponse = crewClient.getAllStudy(1, MAX_TAKE_COUNT, activeGen, active, studyCategory);

        return crewApiResponse.data().meetings().stream().map(responseMapper::toStudyResponse).toList();
    }


}
