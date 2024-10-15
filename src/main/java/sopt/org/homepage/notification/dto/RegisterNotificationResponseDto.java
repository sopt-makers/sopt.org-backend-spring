package sopt.org.homepage.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

@Validated
@Getter
public class RegisterNotificationResponseDto {

    @Schema(description = "Notification ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private int id;

    @Schema(description = "기수 (Generation)", requiredMode = Schema.RequiredMode.REQUIRED)
    private int generation;

    @Schema(description = "이메일 (Email)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "생성일자 (Creation Date)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createdAt;

    public RegisterNotificationResponseDto(int id, int generation, String email, Date createdAt) {
        this.id = id;
        this.generation = generation;
        this.email = email;
        this.createdAt = createdAt;
    }
}

