package com.ptitb22dccn539.quiz.Beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.slugify.Slugify;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.DecimalFormat;

@Configuration
public class BeanAppConfig {
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
}
