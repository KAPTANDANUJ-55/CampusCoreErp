package com.campuscore.attendance.entity;

import com.campuscore.attendance.enums.AttendanceStatus;
import com.campuscore.attendance.enums.StaffRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "staff_attendance")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StaffAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String staffId; // random id that will starts with FAC

    @Column(nullable = false)
    private String staffName;

    @Column(nullable = false)
    private StaffRole role; // FACULTY, PRINCIPAL, STAFF

    @Column(nullable = false)
    private LocalDate date;

    private LocalTime punchInTime;
    private LocalTime punchOutTime;

    private AttendanceStatus status; // PRESENT, LATE, HALF_DAY, ON_LEAVE
}