package com.ptitb22dccn539.quiz.Model.DTO;

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
public class TestDetailDTO {
    private String id;
    private String testId;
    private Date createdDate;
    private Date modifiedDate;
    private String createdBy;
    private List<AnswerSelectedDTO> answers;
}
