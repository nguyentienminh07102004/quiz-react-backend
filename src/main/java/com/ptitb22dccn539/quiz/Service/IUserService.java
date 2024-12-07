package com.ptitb22dccn539.quiz.Service;

import com.ptitb22dccn539.quiz.Model.DTO.UserDTO;
import com.ptitb22dccn539.quiz.Model.Request.User.UserChangePassword;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginRequest;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginWithGithub;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginWithGoogle;
import com.ptitb22dccn539.quiz.Model.Request.User.UserUpdate;
import com.ptitb22dccn539.quiz.Model.Response.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.web.PagedModel;

import java.util.List;

public interface IUserService {
    UserResponse save(UserDTO userDTO);
    String login(UserLoginRequest loginRequest, HttpServletResponse httpServletResponse);
    void deleteUser(List<String> ids);
    UserResponse getUserById(String id);
    UserResponse getUserByEmail(String email);
    void logout();
    void changePassword(UserChangePassword userChangePassword);
    UserResponse updateUser(UserUpdate userUpdate);
    List<UserResponse> getAllUsers();
    PagedModel<UserResponse> getAllUsers(Integer page);
    UserResponse getMyInfo();
    String loginWithGithub(UserLoginWithGithub loginWithGoogle, HttpServletResponse httpServletResponse);
    String loginWithGoogle(UserLoginWithGoogle loginWithGoogle, HttpServletResponse httpServletResponse);
}
