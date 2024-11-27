package com.ptitb22dccn539.quiz.Service.impl;

import com.ptitb22dccn539.quiz.Convertors.MapIfNull;
import com.ptitb22dccn539.quiz.Convertors.UserConvertor;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Exceptions.ServerErrorException;
import com.ptitb22dccn539.quiz.Model.DTO.UserDTO;
import com.ptitb22dccn539.quiz.Model.Entity.JwtTokenEntity;
import com.ptitb22dccn539.quiz.Model.Entity.UserEntity;
import com.ptitb22dccn539.quiz.Model.Request.UserChangePassword;
import com.ptitb22dccn539.quiz.Model.Request.UserLoginRequest;
import com.ptitb22dccn539.quiz.Model.Request.UserUpdate;
import com.ptitb22dccn539.quiz.Model.Response.UserResponse;
import com.ptitb22dccn539.quiz.Repositoty.IUserRepository;
import com.ptitb22dccn539.quiz.Repositoty.JwtTokenRepository;
import com.ptitb22dccn539.quiz.Security.JWTGenerator;
import com.ptitb22dccn539.quiz.Service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ptitb22dccn539.quiz.enums.UserStatus.INACTIVE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final UserConvertor userConvertor;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;
    private final MapIfNull<UserEntity> mapIfNull;
    private final JwtTokenRepository jwtTokenRepository;

    @Override
    @Transactional
    public UserResponse save(UserDTO userDTO) {
        UserEntity user = userConvertor.dtoToEntity(userDTO);
        UserEntity savedUser = userRepository.save(user);
        return userConvertor.entityToResponse(savedUser);
    }

    @Override
    @Transactional
    public String login(UserLoginRequest loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new DataInvalidException("Email or password is invalid!");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new DataInvalidException("Email or password is invalid!");
        }
        if (user.getStatus().equals(INACTIVE)) {
            throw new DataInvalidException("User is not active!");
        }
        if (jwtTokenRepository.existsByUser_Email(user.getEmail())) {
            user.getJwtToken().setUser(null);
            user.setJwtToken(null);
        }
        try {
            Pair<String, String> jwtObject = jwtGenerator.generatorToken(user);
            JwtTokenEntity jwtToken = new JwtTokenEntity(jwtObject.getSecond(), jwtObject.getFirst(), user);
            user.setJwtToken(jwtToken);
            userRepository.save(user);
            return jwtObject.getFirst();
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteUser(List<String> ids) {
        ids.forEach(id -> {
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> new DataInvalidException("User not found!"));
            user.setStatus(INACTIVE);
            userRepository.save(user);
        });
    }

    @Override
    @PostAuthorize(value = "returnObject.getEmail().equals(authentication.name)")
    public UserResponse getUserById(String id) {
        return userConvertor.entityToResponse(this.getUserEntityById(id));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        return userConvertor.entityToResponse(user);
    }

    @Override
    public void logout() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email);
        // set orphan remove
        user.getJwtToken().setUser(null);
        user.setJwtToken(null);
        userRepository.save(user);
    }

    @Override
    public void changePassword(UserChangePassword userChangePassword) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(email);
        if (!passwordEncoder.matches(userChangePassword.getOldPassword(), userEntity.getEmail())) {
            throw new DataInvalidException("Password is invalid!");
        }
        if (passwordEncoder.matches(userChangePassword.getNewPassword(), userEntity.getPassword())) {
            throw new DataInvalidException("New password is same old password!");
        }
        if (!userChangePassword.getNewPassword().equals(userChangePassword.getReNewPassword())) {
            throw new DataInvalidException("New password and repeat new password is not same!");
        }
        userEntity.setPassword(passwordEncoder.encode(userChangePassword.getNewPassword()));
        userRepository.save(userEntity);
        logout();
    }

    @Override
    public UserResponse updateUser(UserUpdate userUpdate) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserEntity user = userRepository.findByEmail(email);
            UserEntity userEntity = userConvertor.updateToEntity(userUpdate);
            UserEntity response = mapIfNull.mapIfNull(user, userEntity);
            UserEntity res = userRepository.save(response);
            return userConvertor.entityToResponse(res);
        } catch (IllegalStateException | IllegalAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public UserEntity getUserEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException("User not found!"));
    }
}
