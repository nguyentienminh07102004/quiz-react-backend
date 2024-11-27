package com.ptitb22dccn539.quiz.Model.Response;

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
public class CategoryResponse {
    private String code;
    private String name;
    private String description;
    private Long numsOfRatings;
    private Double rate;
}
