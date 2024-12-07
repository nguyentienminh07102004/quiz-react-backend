package com.ptitb22dccn539.quiz.Service.impl;

import com.ptitb22dccn539.quiz.Convertors.MapIfNull;
import com.ptitb22dccn539.quiz.Convertors.UserConvertor;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Exceptions.ServerErrorException;
import com.ptitb22dccn539.quiz.Model.DTO.UserDTO;
import com.ptitb22dccn539.quiz.Model.Entity.JwtTokenEntity;
import com.ptitb22dccn539.quiz.Model.Entity.UserEntity;
import com.ptitb22dccn539.quiz.Model.Request.User.UserChangePassword;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginRequest;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginWithGithub;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginWithGoogle;
import com.ptitb22dccn539.quiz.Model.Request.User.UserUpdate;
import com.ptitb22dccn539.quiz.Model.Response.UserResponse;
import com.ptitb22dccn539.quiz.Repositoty.IUserRepository;
import com.ptitb22dccn539.quiz.Repositoty.JwtTokenRepository;
import com.ptitb22dccn539.quiz.Security.JWTGenerator;
import com.ptitb22dccn539.quiz.Service.IUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Value(value = "${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value(value = "${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value(value = "${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;
    @Value(value = "${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    @Override
    @Transactional
    public UserResponse save(UserDTO userDTO) {
        UserEntity user = userConvertor.dtoToEntity(userDTO);
        UserEntity savedUser = userRepository.save(user);
        return userConvertor.entityToResponse(savedUser);
    }

    @Override
    @Transactional
    public String login(UserLoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail());
        if (loginRequest.getLoginSocial() != null && !loginRequest.getLoginSocial()) {
            if (user == null) {
                throw new DataInvalidException("Email or password is invalid!");
            }
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new DataInvalidException("Email or password is invalid!");
            }
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
            Cookie cookie = new Cookie("token", jwtObject.getFirst());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            httpServletResponse.addCookie(cookie);
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
            return null;
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserEntity> list = userRepository.findAll();
        return list.stream()
                .map(userConvertor::entityToResponse)
                .toList();
    }

    @Override
    public PagedModel<UserResponse> getAllUsers(Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<UserEntity> list = userRepository.findAll(pageable);
        Page<UserResponse> pageResponse = list.map(userConvertor::entityToResponse);
        return new PagedModel<>(pageResponse);
    }

    @Override
    public UserResponse getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.getUserByEmail(email);
    }

    @Override
    public String loginWithGithub(UserLoginWithGithub loginWithGithub, HttpServletResponse httpServletResponse) {
        String tokenUri = "https://github.com/login/oauth/access_token";
        Map<String, String> properties = new HashMap<>();
        properties.put("code", loginWithGithub.getCode());
        properties.put("client_id", githubClientId);
        properties.put("client_secret", githubClientSecret);
        properties.put("redirect_uri", "http://localhost:5173/login");
        properties.put("grant_type", AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        return getUserInfo(properties, tokenUri, "https://api.github.com/user", httpServletResponse);
    }

    @Override
    public String loginWithGoogle(UserLoginWithGoogle loginWithGoogle, HttpServletResponse httpServletResponse) {
        String tokenUri = "https://www.googleapis.com/oauth2/v4/token";
        Map<String, String> properties = new HashMap<>();
        properties.put("code", loginWithGoogle.getCode());
        properties.put("client_id", googleClientId);
        properties.put("client_secret", googleClientSecret);
        properties.put("redirect_uri", "http://localhost:5173");
        properties.put("grant_type", AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        return getUserInfo(properties, tokenUri,"https://www.googleapis.com/oauth2/v3/userinfo", httpServletResponse);
    }

    public UserEntity getUserEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException("User not found!"));
    }

    @Transactional
    protected String getUserInfo(Map<String, String> properties, String tokenUri, String userInfoUrl, HttpServletResponse httpServletResponse) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
            properties.forEach(multiValueMap::add);
            HttpEntity<MultiValueMap<String, String>> http = new HttpEntity<>(multiValueMap, httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, http, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                Map<String, Object> data = response.getBody();
                String accessToken = data.get("access_token").toString();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<String> entity = new HttpEntity<>(headers);
                try {
                    RestTemplate template = new RestTemplate();
                    ResponseEntity<Map> responseEntity = template.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);
                    String email = responseEntity.getBody().get("email").toString();
                    String password = UUID.randomUUID().toString();
                    if(userRepository.findByEmail(email) == null) {
                        this.save(new UserDTO(null, null, email, password, password, null, null, null, null, null));
                    }
                    return this.login(new UserLoginRequest(email, password, true), httpServletResponse);
                } catch (Exception e) {
                    return null;
                }
            }
        } catch (Exception exception) {
            return null;
        }
        return null;
    }
}