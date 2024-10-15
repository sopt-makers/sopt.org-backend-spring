package sopt.org.homepage.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class GetNotificationListResponseDto {

    @Schema(description = "기수", example = "34", required = true)
    private Integer generation;

    @Schema(description = "모집알림 신청한 이메일 리스트", example = "[\"example@naver.com\", \"example2@naver.com\", \"example3@naver.com\"]", required = true)
    private List<String> emailList;

    public GetNotificationListResponseDto(Integer generation, List<String> emailList) {
        this.generation = generation;
        this.emailList = emailList;
    }
}