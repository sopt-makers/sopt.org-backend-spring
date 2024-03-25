package sopt.org.homepage.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.repository.SemestersRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("semesters")
@Tag(name = "Semester")
public class SemestersController {
    private final SemestersRepository semestersRepository;

    @GetMapping("")
    public ResponseEntity getSemesters () {
        val response = semestersRepository.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}


