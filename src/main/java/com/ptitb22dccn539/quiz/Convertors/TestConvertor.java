package com.ptitb22dccn539.quiz.Convertors;

import com.ptitb22dccn539.quiz.Model.DTO.TestDTO;
import com.ptitb22dccn539.quiz.Model.Entity.QuestionEntity;
import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Model.Response.QuestionResponse;
import com.ptitb22dccn539.quiz.Model.Response.TestResponse;
import com.ptitb22dccn539.quiz.Service.IQuestionService;
import com.ptitb22dccn539.quiz.enums.Difficulty;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TestConvertor implements IConvertor<TestDTO, TestEntity, TestResponse> {
    private final ModelMapper modelMapper;
    private final IQuestionService questionService;
    private final QuestionConvertor questionConvertor;

    @Override
    public TestEntity dtoToEntity(TestDTO dto) {
        TestEntity testEntity = modelMapper.map(dto, TestEntity.class);
        List<QuestionEntity> questions = dto.getQuestionIds().stream()
                .map(questionService::getQuestionEntityById)
                .toList();
        if(dto.getId() == null) {
            testEntity.setRating(0.0);
            testEntity.setNumsOfRatings(0L);
        }
        Difficulty difficulty = Difficulty.valueOf(dto.getDifficulty());
        testEntity.setDifficulty(difficulty);
        testEntity.setQuestions(questions);
        return testEntity;
    }

    @Override
    public TestResponse entityToResponse(TestEntity entity) {
        TestResponse testResponse = modelMapper.map(entity, TestResponse.class);
        if(entity.getNumsOfRatings() == 0) testResponse.setRate(0.0);
        else testResponse.setRate(entity.getRating() / entity.getNumsOfRatings());
        List<QuestionResponse> questionResponses = entity.getQuestions().stream()
                .map(questionConvertor::entityToResponse)
                .toList();
        testResponse.setQuestionResponses(questionResponses);
        return testResponse;
    }
}
