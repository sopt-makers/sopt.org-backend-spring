package sopt.org.homepage.mapper;

import org.springframework.stereotype.Component;
import sopt.org.homepage.dto.SemesterDao;
import sopt.org.homepage.dto.response.SemestersListResponse;

@Component
public class ResponseMapper {

    public SemestersListResponse.SemestersResponse toSemestersResponse(SemesterDao semester) {
        return new SemestersListResponse.SemestersResponse(
                semester.id(), semester.color(), semester.logo(), semester.background(), semester.name(), semester.year()
        );
    }
}
