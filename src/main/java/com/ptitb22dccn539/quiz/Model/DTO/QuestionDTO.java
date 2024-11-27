package com.ptitb22dccn539.quiz.Model.DTO;

import jakarta.validation.constraints.NotBlank;
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
public class QuestionDTO {
    private String id;
    @NotBlank(message = "Question content not null or empty!")
    private String content;
    private String categoryCode;
    private String title;
    private String shortDescription;
    private List<AnswerDTO> answers;
}
