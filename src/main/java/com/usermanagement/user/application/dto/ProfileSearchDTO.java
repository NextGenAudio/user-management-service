package com.usermanagement.user.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileSearchDTO {
    private Long profileId;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isActive;
    private LocalDate createdAt;
    private String profileImageURL;

}
