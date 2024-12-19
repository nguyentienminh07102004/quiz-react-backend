package com.ptitb22dccn539.quiz.Convertors;

import com.ptitb22dccn539.quiz.Model.Entity.TestRatingEntity;
import com.ptitb22dccn539.quiz.Model.Response.TestRatingResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestRatingConvertor {
    private final ModelMapper modelMapper;

    public TestRatingResponse entityToResponse(TestRatingEntity testRatingEntity) {
        TestRatingResponse ratingResponse = modelMapper.map(testRatingEntity, TestRatingResponse.class);
        ratingResponse.setEmail(testRatingEntity.getUser().getEmail());
        ratingResponse.setTestId(testRatingEntity.getTest().getId());
        return ratingResponse;
    }
}
