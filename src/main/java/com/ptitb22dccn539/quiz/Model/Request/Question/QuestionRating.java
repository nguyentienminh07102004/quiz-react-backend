package com.ptitb22dccn539.quiz.Model.Request.Question;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class QuestionRating {
    @NotNull(message = "Rating is not null")
    @Min(value = 0, message = "Min rating is 0")
    @Max(value = 5, message = "Max rating is 5")
    private Double rating;
    @NotNull(message = "QuestionId is not null or blank!")
    @NotBlank(message = "QuestionId is not null or blank!")
    private String questionId;
}
