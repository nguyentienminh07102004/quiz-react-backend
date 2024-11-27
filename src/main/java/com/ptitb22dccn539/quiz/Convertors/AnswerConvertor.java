package com.ptitb22dccn539.quiz.Convertors;

import com.ptitb22dccn539.quiz.Model.DTO.AnswerDTO;
import com.ptitb22dccn539.quiz.Model.Entity.AnswerEntity;
import com.ptitb22dccn539.quiz.Model.Entity.QuestionEntity;
import com.ptitb22dccn539.quiz.Model.Response.AnswerResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerConvertor {
    private final ModelMapper modelMapper;

    public AnswerEntity dtoToEntity(AnswerDTO dto, QuestionEntity question) {
        AnswerEntity answer = modelMapper.map(dto, AnswerEntity.class);
        answer.setQuestion(question);
        return answer;
    }

    public AnswerResponse entityToResponse(AnswerEntity entity) {
        return modelMapper.map(entity, AnswerResponse.class);
    }
}
