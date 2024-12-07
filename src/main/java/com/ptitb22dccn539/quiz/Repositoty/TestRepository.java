package com.ptitb22dccn539.quiz.Repositoty;

import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Repositoty.Customer.Test.TestRepositoryCustomer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<TestEntity, String>, TestRepositoryCustomer {
    List<TestEntity> findAllByIdNotAndCategories_CodeIn(String testId, List<String> categories, Pageable pageable);
}
