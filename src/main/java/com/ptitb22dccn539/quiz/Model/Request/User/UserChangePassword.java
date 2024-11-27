package com.ptitb22dccn539.quiz.Model.Request.User;

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
public class UserChangePassword {
    @NotBlank
    @Size(min = 8)
    private String oldPassword;
    @NotBlank
    @Size(min = 8)
    private String newPassword;
    @NotBlank
    @Size(min = 8)
    private String reNewPassword;
}
