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
    private final CategoryConvertor categoryConvertor;

    @Override
    public QuestionEntity dtoToEntity(QuestionDTO dto) {
        QuestionEntity questionEntity = modelMapper.map(dto, QuestionEntity.class);
        CategoryEntity category = categoryService.getCategoryEntityByCode(dto.getCategory());
        questionEntity.setCategory(category);
        List<AnswerEntity> answers = dto.getAnswers().stream()
                .map(item -> answerConvertor.dtoToEntity(item, questionEntity))
                .toList();
        questionEntity.setAnswers(answers);
        return questionEntity;
    }

    @Override
    public QuestionResponse entityToResponse(QuestionEntity entity) {
        QuestionResponse questionResponse = modelMapper.map(entity, QuestionResponse.class);
        if (entity.getCategory() != null) {
            questionResponse.setCategory(categoryConvertor.entityToResponse(entity.getCategory()));
        }
        if (entity.getAnswers() != null) {
            List<AnswerResponse> answers = entity.getAnswers().stream()
                    .map(answerConvertor::entityToResponse)
                    .toList();
            questionResponse.setAnswers(answers);
        }
        return questionResponse;
    }
}
