package com.ptitb22dccn539.quiz.Convertors;

import com.ptitb22dccn539.quiz.Model.DTO.QuestionDTO;
import com.ptitb22dccn539.quiz.Model.Entity.AnswerEntity;
import com.ptitb22dccn539.quiz.Model.Entity.CategoryEntity;
import com.ptitb22dccn539.quiz.Model.Entity.QuestionEntity;
import com.ptitb22dccn539.quiz.Model.Response.AnswerResponse;
import com.ptitb22dccn539.quiz.Model.Response.QuestionResponse;
import com.ptitb22dccn539.quiz.Service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionConvertor implements IConvertor<QuestionDTO, QuestionEntity, QuestionResponse> {
    private final ModelMapper modelMapper;
    private final ICategoryService categoryService;
    private final AnswerConvertor answerConvertor;

    @Override
    public QuestionEntity dtoToEntity(QuestionDTO dto) {
        QuestionEntity questionEntity = modelMapper.map(dto, QuestionEntity.class);
        if(dto.getId() == null) {
            questionEntity.setRating(0.0);
            questionEntity.setNumsOfRatings(0L);
        }
        if(!dto.getCategoryCode().isBlank()) {
            CategoryEntity category = categoryService.getCategoryEntityByCode(dto.getCategoryCode());
            questionEntity.setCategory(category);
        }
        List<AnswerEntity> answers = dto.getAnswers().stream()
                .map(item -> answerConvertor.dtoToEntity(item, questionEntity))
                .toList();
        questionEntity.setAnswers(answers);
        return questionEntity;
    }

    @Override
    public QuestionResponse entityToResponse(QuestionEntity entity) {
        QuestionResponse questionResponse = modelMapper.map(entity, QuestionResponse.class);
        if(entity.getCategory() != null) {
            questionResponse.setCategory(entity.getCategory().getName());
        }
        if(entity.getAnswers() != null) {
            List<AnswerResponse> answers = entity.getAnswers().stream()
                    .map(answerConvertor::entityToResponse)
                    .toList();
            questionResponse.setAnswers(answers);
        }
        return questionResponse;
    }
}
