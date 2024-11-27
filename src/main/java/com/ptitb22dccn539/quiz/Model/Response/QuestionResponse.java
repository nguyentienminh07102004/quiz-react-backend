package com.ptitb22dccn539.quiz.Model.Response;

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
public class QuestionResponse {
    private String id;
    private String title;
    private String shortDescription;
    private String category;
    private String content;
    private Long numsOfRatings;
    private Double rate;
    private List<AnswerResponse> answers;
}
