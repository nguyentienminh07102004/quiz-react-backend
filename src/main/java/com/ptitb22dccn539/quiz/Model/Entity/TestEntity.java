package com.ptitb22dccn539.quiz.Model.Entity;

import com.ptitb22dccn539.quiz.enums.Difficulty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Entity
@Table(name = "tests")
@Getter
@Setter
public class TestEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    @Column(name = "difficulty")
    private Difficulty difficulty;

    @ManyToMany
    @JoinTable(name = "question_test",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<QuestionEntity> questions;

    @ManyToMany
    @JoinTable(name = "test_category",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "category_code"))
    private List<CategoryEntity> categories;

    @OneToMany(mappedBy = "test", orphanRemoval = true)
    @Cascade(value = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<TestRatingEntity> testRatings;

    @OneToMany(mappedBy = "test", orphanRemoval = true)
    private List<TestDetailEntity> testDetailEntities;
}
