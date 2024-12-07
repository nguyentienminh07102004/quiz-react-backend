package com.ptitb22dccn539.quiz.Service.impl;

import com.ptitb22dccn539.quiz.Convertors.TestConvertor;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Model.DTO.TestDTO;
import com.ptitb22dccn539.quiz.Model.Entity.CategoryEntity;
import com.ptitb22dccn539.quiz.Model.Entity.QuestionEntity;
import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Model.Request.Test.TestRating;
import com.ptitb22dccn539.quiz.Model.Request.Test.TestSearch;
import com.ptitb22dccn539.quiz.Model.Response.TestResponse;
import com.ptitb22dccn539.quiz.Repositoty.ITestDetailRepository;
import com.ptitb22dccn539.quiz.Repositoty.TestRepository;
import com.ptitb22dccn539.quiz.Service.ITestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements ITestService {
    private final TestRepository testRepository;
    private final TestConvertor testConvertor;
    private final DecimalFormat format;
    private final ITestDetailRepository testDetailRepository;

    @Override
    @Transactional
    public TestResponse save(TestDTO testDTO) {
        if (testDTO.getId() != null) {
            this.getTestEntityById(testDTO.getId());
        }
        TestEntity testEntity = testConvertor.dtoToEntity(testDTO);
        List<QuestionEntity> listQuestions = testEntity.getQuestions();
        List<CategoryEntity> listCategories = listQuestions.stream()
                .map(QuestionEntity::getCategory)
                .collect(Collectors.toSet())
                .stream().toList();
        testEntity.setCategories(listCategories);
        TestEntity savedEntity = testRepository.save(testEntity);
        return testConvertor.entityToResponse(savedEntity);
    }

    @Override
    @Transactional
    public void deleteByIds(List<String> ids) {
        ids.forEach(this::getTestEntityById);
        testRepository.deleteAllById(ids);
    }

    @Override
    public TestEntity getTestEntityById(String id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException("Test not found!"));
    }

    @Override
    public TestResponse getTestResponseById(String id) {
        return testConvertor.entityToResponse(this.getTestEntityById(id));
    }

    @Override
    public PagedModel<TestResponse> getAllTests(Integer page) {
        if (page == null || page < 1) page = 1;
        Pageable pageable = PageRequest.of(page - 1, 8);
        Page<TestEntity> entityPage = testRepository.findAll(pageable);
        Page<TestResponse> responses = entityPage.map(testConvertor::entityToResponse);
        return new PagedModel<>(responses);
    }

    @Override
    public PagedModel<TestResponse> getAllTests(TestSearch testSearch) {
        if(testSearch.getPage() == null || testSearch.getPage() < 1) testSearch.setPage(1);
        Pageable pageable = PageRequest.of(testSearch.getPage() - 1, 6);
        List<TestEntity> testEntities = testRepository.findTest(testSearch, pageable);
        List<TestResponse> listResponse = testEntities.stream()
                .map(testConvertor::entityToResponse)
                .toList();
        Page<TestResponse> page = new PageImpl<>(listResponse, pageable, testRepository.findTest(testSearch, null).size());
        return new PagedModel<>(page);
    }

    public TestResponse rating(TestRating testRating) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long counter = testDetailRepository.countByCreatedBy(email);
        if(counter <= 0) {
            throw new DataInvalidException("You must play first");
        }
        TestEntity testEntity = this.getTestEntityById(testRating.getTestId());
        Double rating = testEntity.getRating() * testEntity.getNumsOfRatings() + testRating.getRating();
        testEntity.setNumsOfRatings(testEntity.getNumsOfRatings() + 1);
        testEntity.setRating(Double.valueOf(format.format(rating / testEntity.getNumsOfRatings())));
        TestEntity response = testRepository.save(testEntity);
        return testConvertor.entityToResponse(response);
    }

    @Override
    public List<TestResponse> getAllTests() {
        List<TestEntity> list = testRepository.findAll();
        return list.stream()
                .map(testConvertor::entityToResponse)
                .toList();
    }

    @Override
    public List<TestResponse> getTestRelated(String testId, List<String> categories) {
        List<TestEntity> list = testRepository.findAllByIdNotAndCategories_CodeIn(testId, categories, PageRequest.of(0, 4));
        return list.stream()
                .map(testConvertor::entityToResponse)
                .toList();
    }
}