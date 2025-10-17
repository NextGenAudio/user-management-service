package com.usermanagement.user;

import com.usermanagement.user.domain.service.MailService;
import com.usermanagement.user.utill.Jwtutil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class MailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    private MailService mailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mailService = new MailService(mailSender);
    }

    @Test
    void sendEmail_Success() {
        String to = "test@test.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        mailService.sendEmail(to, subject, body);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_Failure() {
        String to = "test@test.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doThrow(new RuntimeException("Mail server error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThrows(RuntimeException.class, () ->
                mailService.sendEmail(to, subject, body)
        );
    }

}
