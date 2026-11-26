package com.campuscore.marksservice.service;

import com.campuscore.marksservice.dto.MarkResponse;
import com.campuscore.marksservice.entity.StudentMark;
import com.campuscore.marksservice.repository.MarksRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.List;


@RequiredArgsConstructor

public class MarksService {
    private final MarksRepo marksRepo;




    private List<MarkResponse> getStudentMarks(String enrollmentNo, Integer semester){
        List<StudentMark>  studentMarks = marksRepo.findByenrollmentNo(enrollmentNo);
        if(studentMarks.isEmpty()){
            marksRepo.findByStudentIdAndSemester(enrollmentNo, semester);
        }
         return studentMarks.stream().map(this::mapToResponse).toList();
    }

    private MarkResponse mapToResponse(StudentMark studentMarks) {
         double pct = Math.round((studentMarks.getMarksObtained() / studentMarks.getMaxMarks()) * 10000.0) / 100.0;
        String status = pct >= 40.0 ? "PASS" : "FAIL";

        return MarkResponse.builder().id(studentMarks.getId()).marksObtained(studentMarks.getMarksObtained()).maxMarks(studentMarks.getMaxMarks()).studentId(studentMarks.getEnrollmentNo()).examType(studentMarks.getExamType()).percentage(pct).semester(studentMarks.getSemester()).status(status).uploadedBy(studentMarks.getUploadedBy()).build();
    }

}