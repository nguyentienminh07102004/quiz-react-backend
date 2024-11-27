package com.ptitb22dccn539.quiz.Model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String firstname;
    private String lastname;
    @NotBlank(message = "Email is not empty!")
    @Email(message = "Email is invalid!")
    private String email;
    @NotBlank
    @Size(min = 8, message = "Password is not empty!")
    private String password;
    @Size(min = 8, message = "Repeat password is not empty!")
    private String rePassword;
    private String address;
    private String avatar;
    private Date dateOfBirth;
    private List<String> roles;
    private String status;
}
