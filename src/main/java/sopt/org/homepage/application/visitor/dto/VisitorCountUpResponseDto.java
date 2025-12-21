package sopt.org.homepage.application.visitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VisitorCountUpResponseDto {
    @Schema(description = "성공 여부", example = "Success")
    private final String status;

    public static VisitorCountUpResponseDto of(String status) {
        return new VisitorCountUpResponseDto(status);
    }
}
