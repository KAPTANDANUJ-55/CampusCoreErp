package com.campuscore.attendance.service;

import com.campuscore.attendance.dto.StudentSummaryDto;
import com.campuscore.attendance.entity.StaffAttendance;
import com.campuscore.attendance.entity.StudentAttendance;
import com.campuscore.attendance.enums.AttendanceStatus;
import com.campuscore.attendance.enums.StaffRole;
import com.campuscore.attendance.repository.StaffRepo;
import com.campuscore.attendance.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private StaffRepo  staffRepo;
    private StudentRepo studentRepo;
    public StaffAttendance punchin(String staffId, String staffName, StaffRole role){
        LocalDate today = LocalDate.now();
        if(staffRepo.findByStaffIdAndDate(staffId,today).isPresent()){
            throw  new RuntimeException("Staff already exist");
        }
        LocalTime now = LocalTime.now();
        AttendanceStatus status = now.isAfter((LocalTime.of(9, 15)))
                ? AttendanceStatus.LATE
                : AttendanceStatus.PRESENT;

        StaffAttendance record = StaffAttendance.builder()
                .staffId(staffId)
                .staffName(staffName)
                .role(role)
                .date(today)
                .punchInTime(now)
                .status(status)
                .build();

        return staffRepo.save(record);
    }
    public StaffAttendance punchOut(String staffId) {
        StaffAttendance record = staffRepo.findByStaffIdAndDate(staffId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("No punch-in record found for today!"));
        record.setPunchOutTime(LocalTime.now());
        return staffRepo.save(record);
    }



    public List<StudentAttendance> markAttendanceAtOnce(List<StudentAttendance> records){
        LocalTime cutoffTime = LocalTime.of(11, 15);
        LocalTime currentTime = LocalTime.now();
          records.forEach(record->{
              if(record.getLectureDate()==null){
                  record.setLectureDate(LocalDate.now());
              }
              if(record.getLectureDate()!=null && record.getStatus()==null){
                  record.setStatus(AttendanceStatus.PRESENT);
              }

              if(record.getStatus()==null){
                  if(currentTime.isAfter(cutoffTime)){
                      record.setStatus(AttendanceStatus.LATE);
                  }
                  else {
                      record.setStatus(AttendanceStatus.PRESENT);
                  }
              }
              else{
                  record.setStatus(AttendanceStatus.ABSENT);
              }
          });

          List<StudentAttendance> attendance = records;
          return studentRepo.saveAll(attendance);
    }


    public List<StudentSummaryDto> getAllDefaulters(){
        Map<String,List<StudentAttendance>> byStudent = studentRepo.findAll().stream()
                .collect(Collectors.groupingBy(StudentAttendance::getStudentEnrollment));
        return byStudent.entrySet().stream()
                .map(entry -> buildSummary(entry.getKey(), entry.getValue()))
                .filter(StudentSummaryDto::isDefaulter)
                .toList();
    }

    public StudentSummaryDto getStudentSummary(String enrollment) {
        List<StudentAttendance> all = studentRepo.findByEnrollmentNo(enrollment);
        return buildSummary(enrollment, all);
    }

   public StudentSummaryDto buildSummary(String enrollmentNo , List<StudentAttendance> records){

        long total = records.size();
        long attended = records.stream().filter(record->record.getStatus()==AttendanceStatus.PRESENT || record.getStatus()== AttendanceStatus.LATE).count();

        long onLeave  = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.ON_LEAVE)
                .count();
        long absent = records.stream()
                .filter(r -> r.getStatus() == AttendanceStatus.ABSENT)
                .count();

       double percentage = total == 0 ? 0.0 : Math.round(((attended * 100.0) / total) * 100.0) / 100.0;
       String studentName = records.isEmpty() ? "Unknown" : records.get(0).getStudentName();

       return StudentSummaryDto.builder()
               .enrollmentNo(enrollmentNo)
               .studentName(studentName)
               .totalLectures(total)
               .attendedLectures(attended)
               .absentLectures(absent)
               .leaveLectures(onLeave)
               .percentage(percentage)
               .defaulter(percentage < 75.0)
               .build();


   }


    
}
