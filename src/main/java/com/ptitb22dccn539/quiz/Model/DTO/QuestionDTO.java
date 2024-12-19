package com.ptitb22dccn539.quiz.Model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @NotBlank
    private String category;
    private String title;
    private String shortDescription;
    @NotNull
    @NotEmpty
    private List<AnswerDTO> answers;
}
