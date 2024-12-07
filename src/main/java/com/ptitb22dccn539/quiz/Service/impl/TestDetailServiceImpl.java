package com.ptitb22dccn539.quiz.Service.impl;

import com.ptitb22dccn539.quiz.Convertors.TestDetailConvertor;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Model.DTO.TestDetailDTO;
import com.ptitb22dccn539.quiz.Model.Entity.TestDetailEntity;
import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Model.Response.TestDetailResponse;
import com.ptitb22dccn539.quiz.Repositoty.ITestDetailRepository;
import com.ptitb22dccn539.quiz.Service.ITestDetailService;
import com.ptitb22dccn539.quiz.Service.ITestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestDetailServiceImpl implements ITestDetailService {
    private final ITestDetailRepository testDetailRepository;
    private final TestDetailConvertor testDetailConvertor;
    private final ITestService testService;

    @Override
    @Transactional
    public TestDetailResponse save(TestDetailDTO testDetailDTO) {
        if (testDetailDTO.getId() == null) {
            TestEntity test = testService.getTestEntityById(testDetailDTO.getTestId());
            TestDetailEntity testDetail = new TestDetailEntity();
            testDetail.setTest(test);
            return testDetailConvertor.entityToResponse(testDetailRepository.save(testDetail));
        }
        testDetailRepository.findById(testDetailDTO.getId())
                .orElseThrow(() -> new DataInvalidException("Test detail not found!"));
        TestDetailEntity testDetailEntity = testDetailConvertor.dtoToEntity(testDetailDTO);
        // set time
        Duration duration = Duration.between(testDetailEntity.getCreatedDate().toInstant(), testDetailEntity.getModifiedDate().toInstant());
        Long totalTime = duration.toSeconds();
        testDetailEntity.setTotalTime(totalTime);
        TestDetailEntity savedTestDetail = testDetailRepository.save(testDetailEntity);
        return testDetailConvertor.entityToResponse(savedTestDetail);
    }

    @Override
    public TestDetailResponse getById(String id) {
        TestDetailEntity testDetail = testDetailRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException("Test detail not found!"));
        return testDetailConvertor.entityToResponse(testDetail);
    }

    @Override
    public List<TestDetailResponse> findTopUserHighScore(Integer maxTop, String testId) {
        Pageable pageable = PageRequest.of(0, maxTop);
        List<TestDetailEntity> list = testDetailRepository.findTopByOrderByScoreAscAndTotalTimeDesc(testId, pageable);
        return list.stream()
                .map(testDetailConvertor::entityToResponse)
                .toList();
    }
}
