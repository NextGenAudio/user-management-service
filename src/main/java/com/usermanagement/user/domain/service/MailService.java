package com.usermanagement.user.domain.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private String fromEmail= "dinushkahimesh@gmail.com";

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to,String subject,String body){
        try{
            SimpleMailMessage msg=new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
