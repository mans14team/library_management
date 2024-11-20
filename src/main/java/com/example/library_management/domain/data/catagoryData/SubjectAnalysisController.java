package com.example.library_management.domain.data.catagoryData;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SubjectAnalysisController {

    private final SubjectAnalysisService subjectAnalysisService;

    public SubjectAnalysisController(SubjectAnalysisService subjectAnalysisService) {
        this.subjectAnalysisService = subjectAnalysisService;
    }

    @GetMapping("/analyze-subjects")
    public String analyzeSubjects() {
        try {
            subjectAnalysisService.analyzeSubjectsAndSaveToJson();
            return "Subject count analysis saved to subject_count.json";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while analyzing subjects: " + e.getMessage();
        }
    }
}
