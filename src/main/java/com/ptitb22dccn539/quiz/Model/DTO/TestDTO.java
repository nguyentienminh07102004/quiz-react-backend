package com.ptitb22dccn539.quiz.Model.DTO;

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
public class TestDTO {
    private String id;
    private String title;
    private String description;
    private List<String> questionIds;
    private String difficulty;
}
