package com.ptitb22dccn539.quiz.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class CategoryEntity extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Id
    @Column(name = "code", unique = true)
    private String code;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    @Column(name = "rating")
    private Double rating;
    @Column(name = "nums_of_rating")
    private Long numsOfRatings;

    @OneToMany(mappedBy = "category")
    private List<QuestionEntity> questions;
}
