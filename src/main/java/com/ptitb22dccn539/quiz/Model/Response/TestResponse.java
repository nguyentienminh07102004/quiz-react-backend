package com.ptitb22dccn539.quiz.Model.Response;

import com.ptitb22dccn539.quiz.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResponse {
    private String id;
    private String title;
    private String description;
    private List<TestRatingResponse> ratingResponses;
    private List<QuestionResponse> questionResponses;
    private Difficulty difficulty;
    private List<CategoryResponse> categories;
    private Double rate;
    private List<TestDetailResponse> testDetailResponses;
}
