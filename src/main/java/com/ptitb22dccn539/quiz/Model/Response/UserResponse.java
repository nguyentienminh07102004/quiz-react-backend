package com.ptitb22dccn539.quiz.Model.Response;

import com.ptitb22dccn539.quiz.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String address;
    private String dateOfBirth;
    private String avatar;
    private List<String> roles;
    private UserStatus status;
}
