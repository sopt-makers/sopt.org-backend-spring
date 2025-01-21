package sopt.org.homepage.notification.dto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
public class GetNotificationListRequestDto {

    @Parameter(description = "기수")
    private final Integer generation;

    public GetNotificationListRequestDto(Integer generation) {
        this.generation = generation;

    }
}

