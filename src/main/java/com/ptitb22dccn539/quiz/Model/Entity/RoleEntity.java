package com.ptitb22dccn539.quiz.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {
    @Column(name = "name")
    private String name;
    @Id
    @Column(name = "code")
    private String code;

    @ManyToMany(mappedBy = "roles", targetEntity = UserEntity.class)
    private List<UserEntity> users;

    public RoleEntity(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
