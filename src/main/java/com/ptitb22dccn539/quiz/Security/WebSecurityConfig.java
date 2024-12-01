package com.ptitb22dccn539.quiz.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Model.Response.APIResponse;
import com.ptitb22dccn539.quiz.Repositoty.JwtTokenRepository;
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
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
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

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value(value = "${jwt.SIGNER_KEY}")
    private String SIGNER_KEY;
    @Value(value = "${api.prefix}")
    private String API_PREFIX;
    private final JWTGenerator jwtGenerator;
    private final JwtTokenRepository jwtTokenRepository;
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
                String.format("/%s/tests/", API_PREFIX),
                String.format("/%s/mail/", API_PREFIX)
        };
        URL_PUBLIC_POST = new String[]{
                "/%s/users/login".formatted(API_PREFIX),
                "/%s/users/register".formatted(API_PREFIX),
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
                                .requestMatchers("/%s/test-detail**".formatted(API_PREFIX)).permitAll()
                                .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.PUT, "/%s/(categories|questions|tests)/rate".formatted(API_PREFIX))).access(new WebExpressionAuthorizationManager("not isAnonymous()"))
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
                    return authentication -> {
                        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("Anonymous", null, List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
                        token.setAuthenticated(false);
                        return token;
                    };
            }
            for (String uri : URL_PUBLIC_GET) {
                if (request.getRequestURI().contains(uri) && request.getMethod().equals("GET"))
                    return authentication -> {
                        UsernamePasswordAuthenticationToken token =
                                new UsernamePasswordAuthenticationToken("Anonymous", null,
                                        List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
                        token.setAuthenticated(false);
                        return token;
                    };
            }
            JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder());
            provider.setJwtAuthenticationConverter(jwtAuthenticationConverter());
            return provider::authenticate;
        };
    }
}