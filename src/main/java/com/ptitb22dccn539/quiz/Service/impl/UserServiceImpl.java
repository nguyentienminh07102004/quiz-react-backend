package com.ptitb22dccn539.quiz.Service.impl;

import com.ptitb22dccn539.quiz.Convertors.MapIfNull;
import com.ptitb22dccn539.quiz.Convertors.UserConvertor;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Exceptions.ServerErrorException;
import com.ptitb22dccn539.quiz.Model.DTO.UserDTO;
import com.ptitb22dccn539.quiz.Model.Entity.JwtTokenEntity;
import com.ptitb22dccn539.quiz.Model.Entity.RoleEntity;
import com.ptitb22dccn539.quiz.Model.Entity.UserEntity;
import com.ptitb22dccn539.quiz.Model.Request.User.UserChangePassword;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginRequest;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginWithGithub;
import com.ptitb22dccn539.quiz.Model.Request.User.UserLoginWithGoogle;
import com.ptitb22dccn539.quiz.Model.Request.User.UserUpdate;
import com.ptitb22dccn539.quiz.Model.Response.UserResponse;
import com.ptitb22dccn539.quiz.Repositoty.IRoleRepository;
import com.ptitb22dccn539.quiz.Repositoty.IUserRepository;
import com.ptitb22dccn539.quiz.Repositoty.JwtTokenRepository;
import com.ptitb22dccn539.quiz.Security.JWTGenerator;
import com.ptitb22dccn539.quiz.Service.IUserService;
import com.ptitb22dccn539.quiz.enums.UserStatus;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ptitb22dccn539.quiz.enums.UserStatus.ACTIVE;
import static com.ptitb22dccn539.quiz.enums.UserStatus.INACTIVE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final UserConvertor userConvertor;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;
    private final JwtTokenRepository jwtTokenRepository;
    private final IRoleRepository roleRepository;
    private final MapIfNull<UserEntity> mapIfNull;

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
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DataInvalidException("Email is exists");
        }
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
            jwtTokenRepository.save(jwtToken);
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
            if (user.getJwtToken() != null) {
                user.getJwtToken().setUser(null);
                user.setJwtToken(null);
            }
            user.setStatus(user.getStatus().equals(INACTIVE) ? ACTIVE : INACTIVE);
            userRepository.save(user);
        });
    }

    @Override
    @PostAuthorize(value = "returnObject.getEmail().equals(authentication.name)")
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        return userConvertor.entityToResponse(this.getUserEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        return userConvertor.entityToResponse(user);
    }

    @Override
    @Transactional
    public void logout() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email);
        // set orphan remove
        user.getJwtToken().setUser(null);
        user.setJwtToken(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(UserChangePassword userChangePassword) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null) {
            throw new DataInvalidException("Email not found!");
        }
        if (!passwordEncoder.matches(userChangePassword.getOldPassword(), userEntity.getPassword())) {
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
    @Transactional
    public UserResponse updateUser(UserUpdate userUpdate) {
        try {
            UserEntity source = this.getUserEntityById(userUpdate.getId());
            UserEntity userEntity = userConvertor.updateToEntity(userUpdate);
            if (userEntity.getJwtToken() != null) {
                userEntity.getJwtToken().setUser(null);
                userEntity.setJwtToken(null);
            }
            userEntity = mapIfNull.mapIfNull(source, userEntity);
            UserEntity res = userRepository.save(userEntity);
            return userConvertor.entityToResponse(res);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<UserEntity> list = userRepository.findAll();
        return list.stream()
                .map(userConvertor::entityToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<UserResponse> getAllUsers(Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<UserEntity> list = userRepository.findAll(pageable);
        Page<UserResponse> pageResponse = list.map(userConvertor::entityToResponse);
        return new PagedModel<>(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.getUserByEmail(email);
    }

    @Override
    @Transactional
    public String loginWithGithub(UserLoginWithGithub loginWithGithub, HttpServletResponse httpServletResponse) {
        String tokenUri = "https://github.com/login/oauth/access_token";
        MultiValueMap<String, String> properties = new LinkedMultiValueMap<>();
        properties.add("code", loginWithGithub.getCode());
        properties.add("client_id", githubClientId);
        properties.add("client_secret", githubClientSecret);
        properties.add("redirect_uri", "http://localhost:5173/login");
        properties.add("grant_type", AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        return getUserInfo(properties, tokenUri, "https://api.github.com/user", httpServletResponse);
    }

    @Override
    @Transactional
    public String loginWithGoogle(UserLoginWithGoogle loginWithGoogle, HttpServletResponse httpServletResponse) {
        String tokenUri = "https://www.googleapis.com/oauth2/v4/token";
        MultiValueMap<String, String> properties = new LinkedMultiValueMap<>();
        properties.add("code", loginWithGoogle.getCode());
        properties.add("client_id", googleClientId);
        properties.add("client_secret", googleClientSecret);
        properties.add("redirect_uri", "http://localhost:5173");
        properties.add("grant_type", AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        return getUserInfo(properties, tokenUri, "https://www.googleapis.com/oauth2/v3/userinfo", httpServletResponse);
    }

    public UserEntity getUserEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException("User not found!"));
    }

    @Transactional
    protected String getUserInfo(MultiValueMap<String, String> properties, String tokenUri, String userInfoUrl, HttpServletResponse httpServletResponse) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> http = new HttpEntity<>(properties, httpHeaders);
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
                    if (userRepository.findByEmail(email) == null) {
                        RoleEntity role = roleRepository.findById("USER")
                                .orElseGet(() -> roleRepository.save(new RoleEntity("Người dùng", "USER")));
                        UserEntity user = UserEntity.builder()
                                .email(email)
                                .password(passwordEncoder.encode(password))
                                .status(UserStatus.ACTIVE)
                                .roles(List.of(role))
                                .build();
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
                    }
                    return this.login(new UserLoginRequest(email, password, true), httpServletResponse);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
            }
        } catch (Exception exception) {
            return null;
        }
        return null;
    }
}