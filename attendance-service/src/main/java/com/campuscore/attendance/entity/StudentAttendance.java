package com.campuscore.attendance.entity;

import com.campuscore.attendance.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "student_attendance")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentEnrollment;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String subjectCode;

    @Column(nullable = false)
    private String facultyId;

    @Column(nullable = false)
    private LocalDate lectureDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status; // PRESENT, ABSENT, ON_LEAVE
}