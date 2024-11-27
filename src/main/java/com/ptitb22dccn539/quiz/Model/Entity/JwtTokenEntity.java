package com.ptitb22dccn539.quiz.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jwt_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "jwt_token", columnDefinition = "LONGTEXT")
    private String jwtToken;

    @OneToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private UserEntity user;
}
