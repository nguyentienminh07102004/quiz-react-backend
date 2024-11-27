package com.ptitb22dccn539.quiz.Service.impl;

import com.ptitb22dccn539.quiz.Convertors.TestConvertor;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Model.DTO.TestDTO;
import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Model.Response.TestResponse;
import com.ptitb22dccn539.quiz.Repositoty.ITestRepository;
import com.ptitb22dccn539.quiz.Service.ITestService;
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
public class TestServiceImpl implements ITestService {
    private final ITestRepository testRepository;
    private final TestConvertor testConvertor;

    @Override
    @Transactional
    public TestResponse save(TestDTO testDTO) {
        if(testDTO.getId() != null) {
            this.getTestEntityById(testDTO.getId());
        }
        TestEntity testEntity = testConvertor.dtoToEntity(testDTO);
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
        if(page == null || page < 1) page = 1;
        Pageable pageable = PageRequest.of(page - 1, 8);
        Page<TestEntity> entityPage = testRepository.findAll(pageable);
        Page<TestResponse> responses = entityPage.map(testConvertor::entityToResponse);
        return new PagedModel<>(responses);
    }
}
