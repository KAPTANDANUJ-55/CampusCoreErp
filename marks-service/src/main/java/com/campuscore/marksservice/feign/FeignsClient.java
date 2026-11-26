package com.campuscore.marksservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "authservice")
public interface FeignsClient {


        @GetMapping("api/auth/validate/student/{enrollmentNo}")
        StudentInfoDto getStudentInfo(@PathVariable String enrollmentNo);


            record StudentInfoDto(
                    String enrollmentNo,
                    String fullName,
                    String department,
                    boolean isActive
            ){}

}
