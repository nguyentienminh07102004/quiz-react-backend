package com.ptitb22dccn539.quiz.Controller;

import com.ptitb22dccn539.quiz.Model.DTO.TestDTO;
import com.ptitb22dccn539.quiz.Model.Request.Test.TestRating;
import com.ptitb22dccn539.quiz.Model.Request.Test.TestSearch;
import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import com.ptitb22dccn539.quiz.Model.Response.TestRatingResponse;
import com.ptitb22dccn539.quiz.Model.Response.TestResponse;
import com.ptitb22dccn539.quiz.Service.ITestService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @PermitAll
    public APIResponse getTestById(@PathVariable(value = "id") String id) {
        TestResponse testResponse = testService.getTestResponseById(id);
        return APIResponse.builder()
                .message("SUCCESS")
                .response(testResponse)
                .build();
    }

    @GetMapping(value = "/condition")
    @ResponseStatus(value = HttpStatus.OK)
    @PermitAll
    public APIResponse getAllTests(@ModelAttribute TestSearch testSearch) {
        PagedModel<TestResponse> testList = testService.getAllTests(testSearch);
        return APIResponse.builder()
                .message("SUCCESS")
                .response(testList)
                .build();
    }

    @GetMapping(value = "/related/{testId}")
    public ResponseEntity<APIResponse> getTestRelated(@RequestParam List<String> categories,
                                                      @PathVariable String testId) {
        List<TestResponse> list = testService.getTestRelated(testId, categories);
        APIResponse response = APIResponse.builder()
                .message("SUCCESS")
                .response(list)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/rate/{testId}")
    @PreAuthorize(value = "hasRole('USER')")
    public ResponseEntity<APIResponse> getRatingByTestId(@PathVariable String testId) {
        TestRatingResponse testRatingResponse = testService.getRatingByTestId(testId);
        APIResponse response = APIResponse.builder()
                .message("SUCCESS")
                .response(testRatingResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(value = "/rate")
    public ResponseEntity<APIResponse> ratingTest(@Valid @RequestBody TestRating testRating) {
        testService.rating(testRating);
        APIResponse response = APIResponse.builder()
                .message("SUCCESS")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/{ids}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<APIResponse> deleteTest(@PathVariable List<String> ids) {
       testService.deleteByIds(ids);
        APIResponse response = APIResponse.builder()
                .message("SUCCESS")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
