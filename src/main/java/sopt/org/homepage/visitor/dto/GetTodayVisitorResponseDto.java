package sopt.org.homepage.visitor.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetTodayVisitorResponseDto {
    @Schema(description = "오늘 하루 방문자 수", example = "2024")
    private final Integer count;

    public static GetTodayVisitorResponseDto of(Integer count) {
        return new GetTodayVisitorResponseDto(count);
    }
}
