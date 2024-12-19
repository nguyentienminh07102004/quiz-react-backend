package com.ptitb22dccn539.quiz.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_rating")
@Getter
@Setter
public class TestRatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "rating")
    private Double rating;
    @ManyToOne
    @JoinColumn(name = "test_id")
    private TestEntity test;
    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private UserEntity user;
}
