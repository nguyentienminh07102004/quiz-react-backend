package com.ptitb22dccn539.quiz.Model.Request.Test;

import com.ptitb22dccn539.quiz.enums.Difficulty;
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
public class TestSearch {
    String title;
    Double rating;
    Difficulty difficulty;
    List<String> category;
    Integer page;
}
