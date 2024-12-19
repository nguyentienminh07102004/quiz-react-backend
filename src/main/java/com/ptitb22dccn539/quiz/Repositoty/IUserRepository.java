package com.ptitb22dccn539.quiz.Repositoty;

import com.ptitb22dccn539.quiz.Model.Entity.UserEntity;
import com.ptitb22dccn539.quiz.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByEmail(String email);
    Page<UserEntity> findAllByStatus(UserStatus status, Pageable pageable);
    boolean existsByEmail(String email);
}
