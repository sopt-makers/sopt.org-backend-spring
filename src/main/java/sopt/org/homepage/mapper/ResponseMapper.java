package sopt.org.homepage.mapper;

import lombok.val;
import org.springframework.stereotype.Component;
import sopt.org.homepage.internal.crew.dto.CrewMeetingVo;
import sopt.org.homepage.internal.crew.dto.StudyResponse;
import sopt.org.homepage.semester.dto.SemesterDao;
import sopt.org.homepage.semester.dto.response.SemestersListResponse;

@Component
public class ResponseMapper {

    public SemestersListResponse.SemestersResponse toSemestersResponse(SemesterDao semester) {
        return new SemestersListResponse.SemestersResponse(
                semester.id(), semester.color(), semester.logo(), semester.background(), semester.name(), semester.year()
        );
    }

    public StudyResponse toStudyResponse(CrewMeetingVo meeting) {
        return new StudyResponse(
                meeting.id(),
                meeting.createdGeneration(),
                meeting.joinableParts(),
                meeting.title(),
                meeting.imageURL().isEmpty() ? null : meeting.imageURL().get(0).url(),
                meeting.startDate(),
                meeting.endDate(),
                meeting.appliedInfo().size()
        );
    }
}
