package com.usermanagement.user.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProfileSearchDTO {
    private Long profileId;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String profileImageURL;

}
