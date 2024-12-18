package com.ptitb22dccn539.quiz.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtDecoder jwtDecoder;
    @Value(value = "${api.prefix}")
    private String API_PREFIX;
    private final ObjectMapper objectMapper;
    private String[] URL_PUBLIC_POST;

    private String[] URL_PUBLIC_GET;

    @PostConstruct
    private void setURLPublic() {
        URL_PUBLIC_GET = new String[]{
                "/images/",
                String.format("/%s/categories/all", API_PREFIX),
                String.format("/%s/categories/", API_PREFIX),
                String.format("/%s/tests/all", API_PREFIX),
                String.format("/%s/tests/condition", API_PREFIX),
                String.format("/%s/mail/", API_PREFIX),
                String.format("/%s/test-detail/max-top/", API_PREFIX),
                String.format("/%s/tests/related/", API_PREFIX),
                "/oauth2/authorization/google",
                "/oauth2/authorization/github",
        };
        URL_PUBLIC_POST = new String[]{
                "/%s/users/login".formatted(API_PREFIX),
                "/%s/users/register".formatted(API_PREFIX),
                "/%s/users/login-google".formatted(API_PREFIX),
                "/%s/users/login-github".formatted(API_PREFIX)
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request.requestMatchers(HttpMethod.POST, URL_PUBLIC_POST)
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, URL_PUBLIC_GET)
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/%s/users/logout".formatted(API_PREFIX)).access(new WebExpressionAuthorizationManager("not isAnonymous()"))
                                .requestMatchers(HttpMethod.POST, "/%s/categories/**".formatted(API_PREFIX)).hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/%s/categories/**".formatted(API_PREFIX)).hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/%s/categories/**".formatted(API_PREFIX)).hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.POST, "/%s/test-detail/".formatted(API_PREFIX)).hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/%s/test-detail/{id}".formatted(API_PREFIX)).access(new WebExpressionAuthorizationManager("not isAnonymous()"))
                                .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.PUT, "/%s/(categories|questions|tests)/rate".formatted(API_PREFIX))).access(new WebExpressionAuthorizationManager("not isAnonymous()"))
                                .requestMatchers(HttpMethod.GET, "/%s/test-detail/max-top/**".formatted(API_PREFIX)).permitAll()
                                .requestMatchers(HttpMethod.GET, "/%s/test-detail/tests/{testId}".formatted(API_PREFIX)).permitAll()
                                .requestMatchers("/%s/questions**".formatted(API_PREFIX)).permitAll()
                                .requestMatchers(HttpMethod.GET, "/%s/tests/**".formatted(API_PREFIX)).permitAll()
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
                    return authentication -> {

                        System.out.println(authentication);
                        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("Anonymous", null, List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
                        token.setAuthenticated(false);
                        return token;
                    };
            }
            for (String uri : URL_PUBLIC_GET) {
                if (request.getRequestURI().contains(uri) && request.getMethod().equals("GET"))
                    return authentication -> {
                        String tokenUser = (String) authentication.getPrincipal();
                        UsernamePasswordAuthenticationToken token =
                                new UsernamePasswordAuthenticationToken("Anonymous", null,
                                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
                        token.setAuthenticated(false);
                        return token;
                    };
            }
            JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder);
            provider.setJwtAuthenticationConverter(jwtAuthenticationConverter());
            return provider::authenticate;
        };
    }
}