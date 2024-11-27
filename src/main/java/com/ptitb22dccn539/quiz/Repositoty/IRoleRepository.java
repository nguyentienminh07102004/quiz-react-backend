package com.ptitb22dccn539.quiz.Repositoty;

import com.ptitb22dccn539.quiz.Model.Entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<RoleEntity, String> {
}
