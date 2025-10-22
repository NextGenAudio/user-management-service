package com.usermanagement.user.application.controller;

import com.usermanagement.user.domain.service.MailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailTestController {

    private final MailService mailService;

    public MailTestController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/test")
    public String testMail() {
        try {
            mailService.sendEmail(
                    "kahagalagekasun@gmail.com",
                    "Test Email from Brevo",
                    "Hello! This is a test email from Spring Boot using Brevo SMTP."
            );
            return "✅ Mail sent successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Mail failed: " + e.getMessage();
        }
    }
}
