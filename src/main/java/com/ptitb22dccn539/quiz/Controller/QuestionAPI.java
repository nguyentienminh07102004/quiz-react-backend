package com.ptitb22dccn539.quiz.Controller;

import com.ptitb22dccn539.quiz.Model.DTO.QuestionDTO;
import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import com.ptitb22dccn539.quiz.Model.Response.QuestionResponse;
import com.ptitb22dccn539.quiz.Service.IQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/questions")
public class QuestionAPI {
    private final IQuestionService questionService;

    @PostMapping(value = "/")
    @ResponseStatus(value = HttpStatus.CREATED)
    @PreAuthorize(value = "hasRole('ADMIN')")
    public APIResponse saveQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        QuestionResponse questionResponse = questionService.save(questionDTO);
        return APIResponse.builder()
                .message("CREATE SUCCESS")
                .response(questionResponse)
                .build();
    }

    @PutMapping(value = "/")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize(value = "hasRole('ADMIN')")
    public APIResponse updateQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        QuestionResponse questionResponse = questionService.save(questionDTO);
        return APIResponse.builder()
                .message("UPDATE SUCCESS")
                .response(questionResponse)
                .build();
    }

    @GetMapping(value = "/")
    @ResponseStatus(value = HttpStatus.OK)
    public APIResponse getAllQuestions(@RequestParam(required = false) Integer page) {
        PagedModel<QuestionResponse> questionResponsePagedModel = questionService.getAllQuestions(page);
        return APIResponse.builder()
                .message("SUCCESS")
                .response(questionResponsePagedModel)
                .build();
    }

    @GetMapping(value = "/{id}")
    public APIResponse getQuestionById(@PathVariable String id) {
        QuestionResponse questionResponse = questionService.getQuestionResponseById(id);
        return APIResponse.builder()
                .message("SUCCESS")
                .response(questionResponse)
                .build();
    }

    @GetMapping(value = "/all")
    public APIResponse getAllQuestionsNoPagination() {
        List<QuestionResponse> list = questionService.getAllQuestions();
        return APIResponse.builder()
                .message("SUCCESS")
                .response(list)
                .build();
    }
}
