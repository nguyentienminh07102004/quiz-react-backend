package com.ptitb22dccn539.quiz.Controller;

import com.ptitb22dccn539.quiz.Model.DTO.UserDTO;
import com.ptitb22dccn539.quiz.Model.Request.User.UserChangePassword;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginRequest;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginWithGithub;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginWithGoogle;
import com.ptitb22dccn539.quiz.Model.Request.User.UserUpdate;
import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import com.ptitb22dccn539.quiz.Model.Response.UserResponse;
import com.ptitb22dccn539.quiz.Service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/users")
public class UserAPI {
    private final IUserService userService;

    @PostMapping(value = "/register")
    @ResponseStatus(value = HttpStatus.CREATED)
    public APIResponse saveUser(@Valid @RequestBody UserDTO userDTO) {
        UserResponse userResponse = userService.save(userDTO);
        return APIResponse.builder()
                .message("CREATE SUCCESS")
                .response(userResponse)
                .build();
    }

    @PutMapping(value = "/")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize(value = "not isAnonymous()")
    public APIResponse updateUser(@Valid @RequestBody UserUpdate userUpdate) {
        UserResponse userResponse = userService.updateUser(userUpdate);
        return APIResponse.builder()
                .message("UPDATE SUCCESS")
                .response(userResponse)
                .build();
    }

    @PostMapping(value = "/login")
    @ResponseStatus(value = HttpStatus.OK)
    public APIResponse login(@Valid @RequestBody UserLoginRequest userLoginRequest, HttpServletResponse httpServletResponse) {
        String token = userService.login(userLoginRequest, httpServletResponse);
        return APIResponse.builder()
                .message("LOGIN SUCCESS")
                .response(token)
                .build();
    }

    @DeleteMapping(value = "/{ids}")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize(value = "hasRole('ADMIN')")
    public APIResponse deleteUser(@PathVariable(value = "ids") List<String> ids) {
        userService.deleteUser(ids);
        return APIResponse.builder()
                .message("DELETE SUCCESS")
                .build();
    }

    @PostMapping(value = "/change-password")
    public ResponseEntity<APIResponse> changePassword(@Valid @RequestBody UserChangePassword userChangePassword) {
        userService.changePassword(userChangePassword);
        APIResponse response = APIResponse.builder()
                .message("SUCCESS")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public APIResponse getUserById(@PathVariable(value = "id") String id) {
        UserResponse response = userService.getUserById(id);
        return APIResponse.builder()
                .message("SUCCESS!")
                .response(response)
                .build();
    }

    @PostMapping(value = "/logout")
    public APIResponse logout() {
        userService.logout();
        return APIResponse.builder()
                .message("LOGOUT SUCCESS")
                .build();
    }

    @GetMapping(value = "/")
    public ResponseEntity<APIResponse> getAllUsers(@RequestParam(required = false, defaultValue = "1") Integer page) {
        PagedModel<UserResponse> list = userService.getAllUsers(page);
        APIResponse response = APIResponse.builder()
                .message("SUCCESS")
                .response(list)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/my-info")
    public ResponseEntity<APIResponse> getMyInfo() {
        UserResponse response = userService.getMyInfo();
        APIResponse apiResponse = APIResponse.builder()
                .message("SUCCESS")
                .response(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping(value = "/login-google")
    public ResponseEntity<APIResponse> loginWithGoogle(@Valid @RequestBody UserLoginWithGoogle loginWithGoogle,
                                                       HttpServletResponse httpServletResponse) {
        String token = userService.loginWithGoogle(loginWithGoogle, httpServletResponse);
        APIResponse response = APIResponse.builder()
                .message("SUCCESS")
                .response(token)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/login-github")
    public ResponseEntity<APIResponse> loginWithGithub(@Valid @RequestBody UserLoginWithGithub loginWithGithub,
                                                       HttpServletResponse httpServletResponse) {
        String token = userService.loginWithGithub(loginWithGithub, httpServletResponse);
        APIResponse response = APIResponse.builder()
                .message("SUCCESS")
                .response(token)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
