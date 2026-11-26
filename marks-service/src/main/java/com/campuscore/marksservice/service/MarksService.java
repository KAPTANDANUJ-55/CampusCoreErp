package com.campuscore.marksservice.service;

import com.campuscore.marksservice.dto.BulkMarksRequest;
import com.campuscore.marksservice.dto.MarkResponse;
import com.campuscore.marksservice.entity.StudentMark;
import com.campuscore.marksservice.feign.FeignsClient;
import com.campuscore.marksservice.repository.MarksRepo;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor

public class MarksService {
    private final MarksRepo marksRepo;
   private final FeignsClient authClient;

    @Transactional
    public List<MarkResponse> uploadBulkMarks(BulkMarksRequest req, String teacherId) {
        List<MarkResponse> results = new ArrayList<>();

        for (BulkMarksRequest.StudentScore entry : req.getScores()) {
            String enrollment = entry.getStudentId().trim();

            if (entry.getMarksObtained() > req.getMaxMarks()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Marks obtained cannot exceed maxMarks for student: " + enrollment);
            }


            try {
                authClient.getStudentInfo(enrollment);
            } catch (FeignException.NotFound e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid Student Enrollment Number: " + enrollment);
            }

            StudentMark mark = marksRepo
                    .findByStudentIdAndSubjectCodeAndExamType(
                            enrollment,
                            req.getSubjectCode().trim().toUpperCase(),
                            req.getExamType())
                    .orElse(StudentMark.builder()
                            .enrollmentNo(enrollment)
                            .subjectCode(req.getSubjectCode().trim().toUpperCase())
                            .examType(req.getExamType())
                            .build());

            mark.setSubjectName(req.getSubjectName().trim());
            mark.setSemester(req.getSemester());
            mark.setMarksObtained(entry.getMarksObtained());
            mark.setMaxMarks(req.getMaxMarks());
            mark.setUploadedBy(teacherId);

            results.add(mapToResponse(marksRepo.save(mark)));
        }
        return results;
    }

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