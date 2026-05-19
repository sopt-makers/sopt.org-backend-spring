package sopt.org.homepage.activityschedule;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.activityschedule.dto.BulkCreateActivitySchedulesCommand;

@Slf4j
@RequiredArgsConstructor
@Service
public class ActivityScheduleService {

    private final ActivityScheduleRepository activityScheduleRepository;

    @Transactional(readOnly = true)
    public List<ActivitySchedule> findByGeneration(Integer generationId) {
        return activityScheduleRepository.findByGenerationIdOrderByDisplayOrderAsc(generationId);
    }

    @Transactional
    public void bulkCreate(BulkCreateActivitySchedulesCommand command) {
        activityScheduleRepository.deleteByGenerationId(command.generationId());

        List<ActivitySchedule> schedules = command.activitySchedules().stream()
                .map(s -> ActivitySchedule.create(command.generationId(), s.name(), s.startDate(), s.endDate(), s.displayOrder()))
                .toList();

        activityScheduleRepository.saveAll(schedules);
        log.info("ActivitySchedule 일괄 생성 완료 - count={}", schedules.size());
    }
}
