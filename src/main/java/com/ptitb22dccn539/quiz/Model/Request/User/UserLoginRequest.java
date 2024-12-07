package com.ptitb22dccn539.quiz.Model.Request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
    @NotBlank(message = "Email or password is invalid!")
    @Email(message = "Email or password is invalid!")
    private String email;
    @NotBlank(message = "Email or password is invalid!")
    @Size(min = 8, message = "Email or password is invalid!")
    private String password;
    private Boolean loginSocial;
}
