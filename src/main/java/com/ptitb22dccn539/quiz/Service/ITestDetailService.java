package com.ptitb22dccn539.quiz.Service;

import com.ptitb22dccn539.quiz.Model.DTO.TestDetailDTO;
import com.ptitb22dccn539.quiz.Model.Response.TestDetailResponse;

import java.util.List;

public interface ITestDetailService {
    TestDetailResponse save(TestDetailDTO testDetailDTO);
    TestDetailResponse getById(String id);
    List<TestDetailResponse> findTopUserHighScore(Integer maxTop, String testId);
}
