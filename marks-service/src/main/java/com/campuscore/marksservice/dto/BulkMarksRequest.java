package com.campuscore.marksservice.dto;

import com.campuscore.marksservice.entity.ExamType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class BulkMarksRequest {

    @NotBlank private String subjectCode;
    @NotBlank private String subjectName;
    @NotNull @Min(1) @Max(8) private Integer semester;
    @NotNull private ExamType examType;
    @NotNull @Positive private Double maxMarks;

    @NotEmpty @Valid
    private List<StudentScore> scores;

    @Data
    public static class StudentScore {
        @NotBlank private String studentId;
        @NotNull @Min(0) private Double marksObtained;
    }
}