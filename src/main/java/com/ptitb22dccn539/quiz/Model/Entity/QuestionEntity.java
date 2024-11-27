package com.ptitb22dccn539.quiz.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class QuestionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "title")
    private String title;
    @Column(name = "short_description", columnDefinition = "LONGTEXT")
    private String shortDescription;
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;
    @Column(name = "rating")
    private Double rating;
    @Column(name = "nums_of_rating")
    private Long numsOfRatings;

    @ManyToOne
    @JoinColumn(name = "category_code", referencedColumnName = "code")
    private CategoryEntity category;

    @OneToMany(mappedBy = "question", orphanRemoval = true)
    @Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<AnswerEntity> answers;

    @ManyToMany(mappedBy = "questions")
    private List<TestEntity> tests;
}
