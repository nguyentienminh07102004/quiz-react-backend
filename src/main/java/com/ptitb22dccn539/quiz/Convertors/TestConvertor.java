package com.ptitb22dccn539.quiz.Convertors;

import com.ptitb22dccn539.quiz.Model.DTO.TestDTO;
import com.ptitb22dccn539.quiz.Model.Entity.QuestionEntity;
import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Model.Entity.TestRatingEntity;
import com.ptitb22dccn539.quiz.Model.Response.CategoryResponse;
import com.ptitb22dccn539.quiz.Model.Response.QuestionResponse;
import com.ptitb22dccn539.quiz.Model.Response.TestRatingResponse;
import com.ptitb22dccn539.quiz.Model.Response.TestResponse;
import com.ptitb22dccn539.quiz.Service.IQuestionService;
import com.ptitb22dccn539.quiz.enums.Difficulty;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestConvertor implements IConvertor<TestDTO, TestEntity, TestResponse> {
    private final ModelMapper modelMapper;
    private final IQuestionService questionService;
    private final QuestionConvertor questionConvertor;
    private final CategoryConvertor categoryConvertor;
    private final DecimalFormat decimalFormat;
    private final TestRatingConvertor testRatingConvertor;

    @Override
    public TestEntity dtoToEntity(TestDTO dto) {
        TestEntity testEntity = modelMapper.map(dto, TestEntity.class);
        List<QuestionEntity> questions = dto.getQuestionIds().stream()
                .map(questionService::getQuestionEntityById)
                .toList();
        Difficulty difficulty = Difficulty.valueOf(dto.getDifficulty());
        testEntity.setDifficulty(difficulty);
        testEntity.setQuestions(questions);
        return testEntity;
    }

    @Override
    public TestResponse entityToResponse(TestEntity entity) {
        TestResponse testResponse = modelMapper.map(entity, TestResponse.class);
        List<QuestionResponse> questionResponses = entity.getQuestions().stream()
                .map(questionConvertor::entityToResponse)
                .toList();
        testResponse.setQuestionResponses(questionResponses);
        List<CategoryResponse> categories = entity.getCategories().stream()
                .map(categoryConvertor::entityToResponse)
                .toList();
        testResponse.setCategories(categories);
        // rating
        if(entity.getTestRatings() != null && !entity.getTestRatings().isEmpty()) {
            List<TestRatingResponse> list = new ArrayList<>();
            Double rate = 0.0;
            for(TestRatingEntity testRating : entity.getTestRatings()) {
                list.add(testRatingConvertor.entityToResponse(testRating));
                rate += testRating.getRating();
            }
            testResponse.setRate(Double.parseDouble(decimalFormat.format(rate / entity.getTestRatings().size())));
            testResponse.setRatingResponses(list);
        }
        return testResponse;
    }
}
