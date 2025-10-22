package com.usermanagement.user.application.dto;


import com.usermanagement.user.domain.entity.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private ProfileAdminDTO profile;
    private String stageName;
    private String primaryGenre;
    private String artistBio;
    private String website;
    private String instagram;
    private String youtube;
    private String spotify;
    private String sampleWork;
    private LocalDateTime createdAt;
    private Status status;
}
