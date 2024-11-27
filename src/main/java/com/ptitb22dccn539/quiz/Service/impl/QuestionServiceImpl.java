package com.ptitb22dccn539.quiz.Service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.ptitb22dccn539.quiz.Convertors.AnswerConvertor;
import com.ptitb22dccn539.quiz.Convertors.QuestionConvertor;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Model.DTO.AnswerDTO;
import com.ptitb22dccn539.quiz.Model.DTO.QuestionDTO;
import com.ptitb22dccn539.quiz.Model.Entity.AnswerEntity;
import com.ptitb22dccn539.quiz.Model.Entity.QuestionEntity;
import com.ptitb22dccn539.quiz.Model.Response.QuestionResponse;
import com.ptitb22dccn539.quiz.Repositoty.IAnswerRepository;
import com.ptitb22dccn539.quiz.Repositoty.IQuestionRepository;
import com.ptitb22dccn539.quiz.Service.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements IQuestionService {
    private final IQuestionRepository questionRepository;
    private final QuestionConvertor questionConvertor;
    private final AnswerConvertor answerConvertor;
    private final IAnswerRepository answerRepository;

    @Override
    @Transactional
    public QuestionResponse save(QuestionDTO questionDTO) {
        if(questionDTO.getId() != null) {
            questionRepository.findById(questionDTO.getId())
                    .orElseThrow(() -> new DataInvalidException("Question not found!"));
            questionDTO.getAnswers().stream()
                    .map(AnswerDTO::getId)
                    .filter(StringUtil::notNullNorEmpty)
                    .forEach(id -> answerRepository.findById(id)
                            .orElseThrow(() -> new DataInvalidException("Answer not found!")));
        }
        QuestionEntity question = questionConvertor.dtoToEntity(questionDTO);
        QuestionEntity response = questionRepository.save(question);
        return questionConvertor.entityToResponse(response);
    }

    @Override
    @Transactional
    public void deleteByIds(List<String> ids) {
        ids.forEach(id -> questionRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException("Question not found!")));
        questionRepository.deleteAllById(ids);
    }

    @Override
    @Transactional
    public void deleteAnswersById(String questionId, List<String> answerIds) {
        QuestionEntity question = this.getQuestionEntityById(questionId);
        question.getAnswers().stream()
                .filter(answer -> answerIds.contains(answer.getId()))
                .forEach(answer -> answer.setQuestion(null));
        questionRepository.save(question);
    }

    @Override
    public QuestionResponse addAnswerToQuestion(String questionId, List<AnswerDTO> answers) {
        QuestionEntity question = this.getQuestionEntityById(questionId);
        if(answers != null) {
            List<AnswerEntity> answerEntityList = answers.stream()
                    .map(item -> answerConvertor.dtoToEntity(item, question))
                    .toList();
            question.setAnswers(answerEntityList);
        }
        QuestionEntity questionEntity = questionRepository.save(question);
        return questionConvertor.entityToResponse(questionEntity);
    }

    @Override
    public QuestionEntity getQuestionEntityById(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException("Question not found!"));
    }

    @Override
    public QuestionResponse getQuestionResponseById(String id) {
        return questionConvertor.entityToResponse(this.getQuestionEntityById(id));
    }

    @Override
    public PagedModel<QuestionResponse> getAllQuestions(Integer page) {
        if(page == null || page < 1) page = 1;
        Pageable pageable = PageRequest.of(page - 1, 3);
        Page<QuestionEntity> entityPage = questionRepository.findAll(pageable);
        Page<QuestionResponse> responses = entityPage.map(questionConvertor::entityToResponse);
        return new PagedModel<>(responses);
    }

    @Override
    public List<QuestionResponse> getAllQuestions() {
        List<QuestionEntity> list = questionRepository.findAll();
        return list.stream()
                .map(questionConvertor::entityToResponse)
                .toList();
    }
}
