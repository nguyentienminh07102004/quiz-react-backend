package com.ptitb22dccn539.quiz.Repositoty;

import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITestRepository extends JpaRepository<TestEntity, String> {
}
