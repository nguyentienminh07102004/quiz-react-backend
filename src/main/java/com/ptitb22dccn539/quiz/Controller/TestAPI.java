package com.ptitb22dccn539.quiz.Controller;

import com.ptitb22dccn539.quiz.Model.DTO.TestDTO;
import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import com.ptitb22dccn539.quiz.Model.Response.TestResponse;
import com.ptitb22dccn539.quiz.Service.ITestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/tests")
public class TestAPI {
    private final ITestService testService;

    @PostMapping(value = "/")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PreAuthorize(value = "hasRole('ADMIN')")
    public APIResponse saveTest(@Valid @RequestBody TestDTO testDTO) {
        TestResponse testResponse = testService.save(testDTO);
        return APIResponse.builder()
                .message("CREATE SUCCESS")
                .response(testResponse)
                .build();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize(value = "hasAnyRole('ADMIN', 'USER')")
    public APIResponse getTestById(@PathVariable(value = "id") String id) {
        TestResponse testResponse = testService.getTestResponseById(id);
        return APIResponse.builder()
                .message("SUCCESS")
                .response(testResponse)
                .build();
    }

    @GetMapping(value = "/")
    @ResponseStatus(value = HttpStatus.OK)
    public APIResponse getAllTests(@RequestParam(required = false) Integer page) {
        PagedModel<TestResponse> testList = testService.getAllTests(page);
        return APIResponse.builder()
                .message("SUCCESS")
                .response(testList)
                .build();
    }
}
