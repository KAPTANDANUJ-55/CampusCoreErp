package com.campuscore.marksservice.dto;

import com.campuscore.marksservice.entity.ExamType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarkResponse {
    private Long id;
    private String studentId;
    private String subjectCode;
    private String subjectName;
    private Integer semester;
    private ExamType examType;
    private Double marksObtained;
    private Double maxMarks;
    private Double percentage;
    private String status;
    private String uploadedBy;
}