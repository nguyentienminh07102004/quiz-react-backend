package com.ptitb22dccn539.quiz.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import com.ptitb22dccn539.quiz.Repositoty.JwtTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value(value = "${jwt.SIGNER_KEY}")
    private String SIGNER_KEY;
    @Value(value = "${api.prefix}")
    private String API_PREFIX;
    private final JWTGenerator jwtGenerator;
    private final JwtTokenRepository jwtTokenRepository;
    private final ObjectMapper objectMapper;

    private final String[] URL_PUBLIC_POST = {
            "/api/users/login",
            "/api/users/register",
    };

    private final String[] URL_PUBLIC_GET = {
            "/images/",
            "/api/categories/all",
            "/api/categories/",
            "/api/tests/all",
            "/api/tests/"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(HttpMethod.POST, "/api/users/login",
                                        "/api/users/register",
                                        "/api/users/logout").permitAll()
                                .requestMatchers(HttpMethod.GET, "/images/**",
                                        "/api/categories/**",
                                        "/api/tests/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/%s/users/logout".formatted(API_PREFIX)).hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/%s/categories/**".formatted(API_PREFIX)).hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/%s/categories/**".formatted(API_PREFIX)).hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/%s/categories/**".formatted(API_PREFIX)).hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/%s/test-detail/".formatted(API_PREFIX)).hasRole("USER")
                                .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> {
                    // có check het han duoc
                    oauth2
                            .authenticationManagerResolver(authenticationManagerResolver());
//                            .jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
//                                    .jwtAuthenticationConverter(jwtAuthenticationConverter()));
                    oauth2
                            .bearerTokenResolver(bearerTokenResolver())
                            .authenticationEntryPoint(((request, response, authException) -> {
                                APIResponse apiResponse = APIResponse.builder()
                                        .message("You must login first!")
                                        .response(authException.getMessage())
                                        .build();
                                response.setStatus(401);
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                                response.getWriter().flush();
                            }));
                })
                .cors(cors -> corsFilter())
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    APIResponse apiResponse = APIResponse.builder()
                                            .message("You are not allowed to access this resource")
                                            .response(accessDeniedException.getMessage())
                                            .build();
                                    response.setStatus(403);
                                    response.setContentType("application/json");
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                                    response.getWriter().flush();
                                })
                );
//        httpSecurity.oauth2Login(AbstractAuthenticationFilterConfigurer::permitAll);
        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            try {
                JWTClaimsSet claimsSet = jwtGenerator.getClaimSet(token);
                // jwt tu kiem tra het han chua
                if (claimsSet.getJWTID() != null && !jwtTokenRepository.existsById(claimsSet.getJWTID())) {
                    throw new DataInvalidException("Token is invalid!");
                }
                SecretKeySpec spec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
                return NimbusJwtDecoder.withSecretKey(spec)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build()
                        .decode(token);
            } catch (JOSEException | ParseException e) {
                throw new DataInvalidException("Token is invalid");
            }
        };
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource url = new UrlBasedCorsConfigurationSource();
        url.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(url);
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return request -> {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        return cookie.getValue();
                    }
                }
            }
            return null;
        };
    }

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
        return request -> {
            for (String uri : URL_PUBLIC_POST) {
                if (request.getRequestURI().contains(uri) && request.getMethod().equals("POST"))
                    return authentication -> authentication;
            }
            for (String uri : URL_PUBLIC_GET) {
                if (request.getRequestURI().equals(uri) && request.getMethod().equals("GET"))
                    return authentication -> authentication;
            }
            JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder());
            provider.setJwtAuthenticationConverter(jwtAuthenticationConverter());
            return provider::authenticate;
        };
    }
}