package com.ptitb22dccn539.quiz.Repositoty;

import com.ptitb22dccn539.quiz.Model.Entity.TestRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITestRatingRepository extends JpaRepository<TestRatingEntity, String> {
    Optional<TestRatingEntity> findByUser_EmailAndTest_Id(String email, String testId);
}
