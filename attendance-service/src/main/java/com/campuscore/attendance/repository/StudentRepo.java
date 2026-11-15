package com.campuscore.attendance.repository;

import com.campuscore.attendance.entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<StudentAttendance, Integer> {
        List<StudentAttendance> findByStudentEnrollment(String enrollmentNo);
}
