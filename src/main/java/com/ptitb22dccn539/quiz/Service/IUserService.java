package com.ptitb22dccn539.quiz.Service;

import com.ptitb22dccn539.quiz.Model.DTO.UserDTO;
import com.ptitb22dccn539.quiz.Model.Request.User.UserChangePassword;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginRequest;
import com.ptitb22dccn539.quiz.Model.Request.User.UserUpdate;
import com.ptitb22dccn539.quiz.Model.Response.UserResponse;

import java.util.List;

public interface IUserService {
    UserResponse save(UserDTO userDTO);
    String login(UserLoginRequest loginRequest);
    void deleteUser(List<String> ids);
    UserResponse getUserById(String id);
    UserResponse getUserByEmail(String email);
    void logout();
    void changePassword(UserChangePassword userChangePassword);
    UserResponse updateUser(UserUpdate userUpdate);
}
