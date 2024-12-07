package com.ptitb22dccn539.quiz.Repositoty.Customer.Test;

import com.ptitb22dccn539.quiz.Model.Entity.TestEntity;
import com.ptitb22dccn539.quiz.Model.Request.Test.TestSearch;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
public class TestRepositoryCustomerImpl implements TestRepositoryCustomer {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<TestEntity> findTest(TestSearch testSearch, Pageable pageable) {
        StringBuilder sql = new StringBuilder("SELECT * FROM Tests t WHERE 1 = 1 ");
        if (!CollectionUtils.isEmpty(testSearch.getCategory())) {
            sql.append(" AND t.id IN ( SELECT test_category.test_id FROM test_category WHERE category_code IN ( ").append(String.join(", ", testSearch.getCategory().stream().map(item -> "'" + item + "'").toList())).append(" )) ");
        }
        if (StringUtils.isNotBlank(testSearch.getTitle())) {
            sql.append(" AND t.title LIKE '%").append(testSearch.getTitle()).append("%' ");
        }
        if (testSearch.getRating() != null) {
            sql.append(" AND t.rating >= ").append(testSearch.getRating());
        }
        if (testSearch.getDifficulty() != null) {
            int difficulty = switch (testSearch.getDifficulty()) {
                case VERY_EASY -> 0;
                case EASY -> 1;
                case MEDIUM -> 2;
                case HARD -> 3;
                case VERY_HARD -> 4;
            };
            sql.append(" AND t.difficulty = ").append(difficulty);
        }
        if(pageable != null) {
            sql.append(" LIMIT ").append(pageable.getPageSize()).append(" OFFSET ").append(pageable.getOffset());
        }
        Query query = entityManager.createNativeQuery(sql.toString(), TestEntity.class);
        return query.getResultList();
    }
}
