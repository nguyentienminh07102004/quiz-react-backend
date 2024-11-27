package com.ptitb22dccn539.quiz.Service;

import com.ptitb22dccn539.quiz.Model.DTO.AnswerDTO;
import com.ptitb22dccn539.quiz.Model.DTO.QuestionDTO;
import com.ptitb22dccn539.quiz.Model.Entity.QuestionEntity;
import com.ptitb22dccn539.quiz.Model.Response.QuestionResponse;
import org.springframework.data.web.PagedModel;

import java.util.List;

public interface IQuestionService {
    QuestionResponse save(QuestionDTO questionDTO);
    void deleteByIds(List<String> ids);
    void deleteAnswersById(String questionId, List<String> answerIds);
    QuestionResponse addAnswerToQuestion(String questionId, List<AnswerDTO> answers);
    QuestionEntity getQuestionEntityById(String id);
    QuestionResponse getQuestionResponseById(String id);
    PagedModel<QuestionResponse> getAllQuestions(Integer page);
    List<QuestionResponse> getAllQuestions();
}
