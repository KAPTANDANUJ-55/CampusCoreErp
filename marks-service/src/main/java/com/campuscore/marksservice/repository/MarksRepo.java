package com.campuscore.marksservice.repository;

import com.campuscore.marksservice.entity.ExamType;

import com.campuscore.marksservice.entity.StudentMark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MarksRepo extends JpaRepository<StudentMark, Long> {

    Optional<StudentMark> findByStudentIdAndSubjectCodeAndExamType(
            String studentId, String subjectCode, ExamType examType);

    List<StudentMark> findByenrollmentNo(String enrollmentNo);

    List<StudentMark> findByStudentIdAndSemester(String studentId, Integer semester);

    List<StudentMark> findBySubjectCodeAndExamType(String subjectCode, ExamType examType);
}