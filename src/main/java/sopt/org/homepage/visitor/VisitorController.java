package sopt.org.homepage.visitor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.visitor.dto.GetTodayVisitorResponseDto;
import sopt.org.homepage.visitor.dto.VisitorCountUpResponseDto;



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