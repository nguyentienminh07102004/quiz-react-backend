package com.ptitb22dccn539.quiz.Repositoty;

import com.ptitb22dccn539.quiz.Model.Entity.JwtTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtTokenRepository extends JpaRepository<JwtTokenEntity, String> {
    boolean existsByUser_Email(String email);
    void deleteByUser_Email(String email);
}
