package com.usermanagement.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Mailtrap SMTP settings
        mailSender.setHost("smtp-relay.brevo.com");
        mailSender.setPort(587);
        mailSender.setUsername("956787003@smtp-brevo.com");            // replace with your Mailtrap username
        mailSender.setPassword("4DTk5nFStO8ydAMY"); // replace with your Mailtrap token

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // optional, prints SMTP debug info

        return mailSender;
    }
}
