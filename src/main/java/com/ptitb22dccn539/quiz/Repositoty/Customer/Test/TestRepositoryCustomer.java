package com.ptitb22dccn539.quiz.Repositoty.Customer.Test;

import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Model.Request.Test.TestSearch;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TestRepositoryCustomer {
    List<TestEntity> findTest(TestSearch testSearch, Pageable pageable);
}
