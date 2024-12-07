package com.ptitb22dccn539.quiz.Beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.slugify.Slugify;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Repositoty.JwtTokenRepository;
import com.ptitb22dccn539.quiz.Security.JWTGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.text.DecimalFormat;
import java.text.ParseException;

@Configuration
@RequiredArgsConstructor
public class BeanAppConfig {
    private final JWTGenerator jwtGenerator;
    private final JwtTokenRepository jwtTokenRepository;
    @Value(value = "${jwt.SIGNER_KEY}")
    private String SIGNER_KEY;
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    @Bean
    public DecimalFormat decimalFormat() {
        return new DecimalFormat("#.##");
    }
    @Bean
    public Slugify slugify() {
        return Slugify.builder().build();
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
}
