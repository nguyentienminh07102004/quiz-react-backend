package com.ptitb22dccn539.quiz.Controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/mail")
public class JavaSendMail {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @GetMapping(value = "/")
    public void sendMail() {
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo("nguyentienminhntm07102004@gmail.com");
            mimeMessageHelper.setSubject("Quiz Full");
            String content = templateEngine.process("test/test", new Context());
            mimeMessageHelper.setText(content, true);
            this.mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }
}
