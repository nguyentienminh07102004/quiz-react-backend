package com.ptitb22dccn539.quiz.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Entity
@Table(name = "test_detail")
@Getter
@Setter
public class TestDetailEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "score")
    private Double score;
    @ManyToOne
    @JoinColumn(name = "test_id")
    private TestEntity test;
    @Column(name = "total_time")
    private Long totalTime;
    @OneToMany(mappedBy = "testDetail")
    @Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<AnswerSelectedEntity> answerSelected;
}