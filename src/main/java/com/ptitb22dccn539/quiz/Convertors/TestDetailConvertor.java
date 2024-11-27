package com.ptitb22dccn539.quiz.Convertors;

import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Model.DTO.AnswerSelectedDTO;
import com.ptitb22dccn539.quiz.Model.DTO.TestDetailDTO;
import com.ptitb22dccn539.quiz.Model.Entity.AnswerEntity;
import com.ptitb22dccn539.quiz.Model.Entity.AnswerSelectedEntity;
import com.ptitb22dccn539.quiz.Model.Entity.QuestionEntity;
import com.ptitb22dccn539.quiz.Model.Entity.TestDetailEntity;
import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Model.Response.AnswerSelectedResponse;
import com.ptitb22dccn539.quiz.Model.Response.TestDetailResponse;
import com.ptitb22dccn539.quiz.Repositoty.IAnswerRepository;
import com.ptitb22dccn539.quiz.Repositoty.IQuestionRepository;
import com.ptitb22dccn539.quiz.Service.ITestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDetailConvertor implements IConvertor<TestDetailDTO, TestDetailEntity, TestDetailResponse> {
    private final ModelMapper modelMapper;
    private final IQuestionRepository questionRepository;
    private final IAnswerRepository answerRepository;
    private final TestConvertor testConvertor;
    private final ITestService testService;

    @Override
    public TestDetailEntity dtoToEntity(TestDetailDTO dto) {
        TestDetailEntity testDetail = modelMapper.map(dto, TestDetailEntity.class);
        TestEntity test = testService.getTestEntityById(dto.getTestId());
        testDetail.setTest(test);
        List<AnswerSelectedDTO> selectedDTOs = dto.getAnswers();
        double score = 0.0D;
        List<AnswerSelectedEntity> answerList = new ArrayList<>();
        for (AnswerSelectedDTO answerSelectedDTO : selectedDTOs) {
            QuestionEntity question = questionRepository
                    .findById(answerSelectedDTO.getQuestionId())
                    .orElseThrow(() -> new DataInvalidException("Question not found!"));
            List<String> answerIds = answerSelectedDTO.getAnswerIds(); // get answer user selected
            for (String id : answerIds) {
                AnswerEntity answer = answerRepository.findById(id)
                        .orElseThrow(() -> new DataInvalidException("Answer not found!"));
                AnswerSelectedEntity answerSelectedEntity = new AnswerSelectedEntity();
                answerSelectedEntity.setAnswer(answer);
                answerSelectedEntity.setTestDetail(testDetail);
                answerList.add(answerSelectedEntity);
            }
            List<String> answerIdsSuccess = question.getAnswers().stream()
                    .filter(AnswerEntity::getIsCorrect)
                    .map(AnswerEntity::getId)
                    .toList(); // get answer id success
            // score
            int totalAnswerSuccess = 0;
            if (answerIdsSuccess.size() != answerIds.size()) continue;
            for (String id : answerIds) {
                if (answerIdsSuccess.contains(id)) {
                    totalAnswerSuccess++;
                } else {
                    totalAnswerSuccess = 0;
                    break;
                }
            }
            score += 1.0 * totalAnswerSuccess / answerIdsSuccess.size();
        }
        testDetail.setScore(score);
        testDetail.setAnswerSelected(answerList);
        return testDetail;
    }

    @Override
    public TestDetailResponse entityToResponse(TestDetailEntity entity) {
        TestDetailResponse testDetailResponse = modelMapper.map(entity, TestDetailResponse.class);
        testDetailResponse.setTest(testConvertor.entityToResponse(entity.getTest()));
        if (entity.getAnswerSelected() != null) {
            List<AnswerSelectedEntity> answerSelected = entity.getAnswerSelected();
            List<AnswerSelectedResponse> answerSelectedResponses = new ArrayList<>();
            TestEntity test = entity.getTest();
            List<QuestionEntity> questionEntities = test.getQuestions();
            for (QuestionEntity questionEntity : questionEntities) {
                // Lay ra danh sach cac cau tra loi theo cau hoi
                List<String> answerEntities = answerSelected.stream()
                        .map(AnswerSelectedEntity::getAnswer)
                        .filter(answer -> answer.getQuestion().getId().equals(questionEntity.getId()))
                        .map(AnswerEntity::getId)
                        .toList();
                List<String> answerCorrect = questionEntity.getAnswers().stream()
                        .filter(AnswerEntity::getIsCorrect)
                        .map(AnswerEntity::getId)
                        .toList();
                boolean isCorrect = answerEntities.size() == answerCorrect.size();
                if(isCorrect) {
                    for(String answerId : answerEntities) {
                        if(!answerCorrect.contains(answerId)) {
                            isCorrect = false;
                            break;
                        }
                    }
                }
                AnswerSelectedResponse answerSelectedResponse = AnswerSelectedResponse.builder()
                        .questionId(questionEntity.getId())
                        .answerIds(answerEntities)
                        .isCorrect(isCorrect)
                        .build();
                answerSelectedResponses.add(answerSelectedResponse);
            }
            testDetailResponse.setAnswers(answerSelectedResponses);
        }
        return testDetailResponse;
    }
}
