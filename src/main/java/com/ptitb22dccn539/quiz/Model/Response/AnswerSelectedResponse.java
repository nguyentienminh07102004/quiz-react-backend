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
public class AnswerSelectedResponse {
    private String questionId;
    private Boolean isCorrect;
    private List<String> answerIds;
}
