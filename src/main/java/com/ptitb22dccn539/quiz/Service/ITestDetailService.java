package com.ptitb22dccn539.quiz.Service;

import com.ptitb22dccn539.quiz.Model.DTO.TestDetailDTO;
import com.ptitb22dccn539.quiz.Model.Response.TestDetailResponse;

public interface ITestDetailService {
    TestDetailResponse save(TestDetailDTO testDetailDTO);
    TestDetailResponse getById(String id);
}
