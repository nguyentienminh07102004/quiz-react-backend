package com.ptitb22dccn539.quiz.Repositoty;

import com.ptitb22dccn539.quiz.Model.Entity.TestDetailEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITestDetailRepository extends JpaRepository<TestDetailEntity, String> {
    @Query(value = "SELECT t FROM TestDetailEntity t WHERE t.test.id = :testId ORDER BY t.score DESC, t.totalTime ASC")
    List<TestDetailEntity> findTopByOrderByScoreAscAndTotalTimeDesc(String testId, Pageable pageable);
    Long countByCreatedBy(String createdBy);
    List<TestDetailEntity> findByCreatedBy(String createdBy);
    List<TestDetailEntity> findByTest_Id(String id);
}
