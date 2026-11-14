package com.campuscore.attendance.repository;

import com.campuscore.attendance.entity.StaffAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StaffRepo extends JpaRepository<StaffAttendance, Integer> {
   Optional<StaffAttendance> findByStaffIdAndDate(String staffId, LocalDate date);
}
