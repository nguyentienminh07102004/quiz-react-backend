package com.ptitb22dccn539.quiz.Model.Request.Category;

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
public class CategoryRating {
    @NotNull(message = "Category code is not null or blank!")
    @NotBlank(message = "Category code is not null or blank!")
    private String categoryCode;
    @NotNull(message = "Rating is not null!")
    @NotNull(message = "Rating is not null")
    @Min(value = 0, message = "Min rating is 5")
    @Max(value = 5, message = "Max rating is 5")
    private Double rating;
}