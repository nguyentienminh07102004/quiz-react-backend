package com.ptitb22dccn539.quiz.Service;

import com.ptitb22dccn539.quiz.Model.DTO.TestDTO;
import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Model.Request.Test.TestRating;
import com.ptitb22dccn539.quiz.Model.Request.Test.TestSearch;
import com.ptitb22dccn539.quiz.Model.Response.TestRatingResponse;
import com.ptitb22dccn539.quiz.Model.Response.TestResponse;
import org.springframework.data.web.PagedModel;

import java.util.List;

public interface ITestService {
    TestResponse save(TestDTO testDTO);
    void deleteByIds(List<String> ids);
    TestEntity getTestEntityById(String id);
    TestResponse getTestResponseById(String id);
    PagedModel<TestResponse> getAllTests(Integer page);
    PagedModel<TestResponse> getAllTests(TestSearch testSearch);
    void rating(TestRating testRating);
    List<TestResponse> getAllTests();
    List<TestResponse> getTestRelated(String testId, List<String> categories);
    TestRatingResponse getRatingByTestId(String testId);
}
