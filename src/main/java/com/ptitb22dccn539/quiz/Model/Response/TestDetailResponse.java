package com.ptitb22dccn539.quiz.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDetailResponse {
    private String id;
    private Double score;
    private TestResponse test;
    private Date createdDate;
    private String createdBy;
    private Long totalTime;
    private List<AnswerSelectedResponse> answers;
}
