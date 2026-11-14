package com.campuscore.attendance.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class StudentSummaryDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String studentName;

    private String enrollmentNo;
    private long attendedLectures;
    private long absentLectures;
    private long leaveLectures;
    private double percentage;
    private boolean defaulter;
    private long totalLectures;

}
