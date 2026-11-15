package com.campuscore.attendance.controller;

import com.campuscore.attendance.dto.StudentSummaryDto;
import com.campuscore.attendance.entity.StaffAttendance;
import com.campuscore.attendance.entity.StudentAttendance;
import com.campuscore.attendance.enums.StaffRole;
import com.campuscore.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService service;
    @org.springframework.beans.factory.annotation.Value("${server.port}")
    private String serverPort;
    @PostMapping("/staff/punch-in")
    public ResponseEntity<StaffAttendance> punchIn(
            @RequestParam String staffId,
            @RequestParam String staffName,
            @RequestParam StaffRole role) {
        return ResponseEntity.ok(service.punchin(staffId, staffName, role));
    }

    @PostMapping("/staff/punch-out")
    public ResponseEntity<StaffAttendance> punchOut(@RequestParam String staffId) {
        return ResponseEntity.ok(service.punchOut(staffId));
    }

    @PostMapping("/student/batch")
    public ResponseEntity<List<StudentAttendance>> markBatch(@RequestBody List<StudentAttendance> records) {
        return ResponseEntity.ok(service.markAttendanceAtOnce(records));
    }

    @GetMapping("/student/{enrollment}")
    public ResponseEntity<StudentSummaryDto> getStudentStats(@PathVariable String enrollment) {
        return ResponseEntity.ok(service.getStudentSummary(enrollment));
    }

    @GetMapping("/defaulters")
    public ResponseEntity<List<StudentSummaryDto>> getDefaulters() {
        return ResponseEntity.ok(service.getAllDefaulters());
    }
        @GetMapping("/info")
    public ResponseEntity<String> getInfoOfInstanceRunning(){
        return ResponseEntity.ok("Handled By Attendance Service Running On Port: "+serverPort);
    }
}