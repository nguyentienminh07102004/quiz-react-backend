package com.ptitb22dccn539.quiz.Controller;

import com.ptitb22dccn539.quiz.Model.DTO.TestDetailDTO;
import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import com.ptitb22dccn539.quiz.Model.Response.TestDetailResponse;
import com.ptitb22dccn539.quiz.Service.ITestDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/test-detail")
public class TestDetailAPI {
    private final ITestDetailService testDetailService;

    @PostMapping(value = "/")
    @ResponseStatus(value = HttpStatus.CREATED)
    public APIResponse save(@Valid @RequestBody TestDetailDTO testDetailDTO) {
        TestDetailResponse testDetailResponse = testDetailService.save(testDetailDTO);
        return APIResponse.builder()
                .message("CREATE SUCCESS")
                .response(testDetailResponse)
                .build();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public APIResponse getById(@PathVariable String id) {
        TestDetailResponse testDetailResponse = testDetailService.getById(id);
        return APIResponse.builder()
                .message("SUCCESS")
                .response(testDetailResponse)
                .build();
    }
}
