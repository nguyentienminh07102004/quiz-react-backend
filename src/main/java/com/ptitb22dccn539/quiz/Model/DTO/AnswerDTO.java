package com.ptitb22dccn539.quiz.Model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDTO {
    private String id;
    @NotBlank(message = "Answer content not null or empty!")
    private String content;
    @NotNull(message = "Answer check isCorrect is not null!")
    private Boolean isCorrect;
}
