package sopt.org.homepage.application.visitor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.application.visitor.dto.GetTodayVisitorResponseDto;
import sopt.org.homepage.application.visitor.dto.VisitorCountUpResponseDto;
import sopt.org.homepage.application.visitor.service.VisitorService;


@RestController
@RequiredArgsConstructor
@RequestMapping("visitor")
@Tag(name = "Visitor")
public class VisitorController {
    private final VisitorService visitorService;

    @Operation(summary = "하루 방문자 수 증가")
    @PostMapping
    public ResponseEntity<VisitorCountUpResponseDto> visitorCountUp(
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(visitorService.visitorCountUp(request));
    }

    @Operation(summary = "하루 방문자 수 조회")
    @GetMapping
    public ResponseEntity<GetTodayVisitorResponseDto> getTodayVisitor() {
        return ResponseEntity.ok(visitorService.getTodayVisitor());
    }
}
