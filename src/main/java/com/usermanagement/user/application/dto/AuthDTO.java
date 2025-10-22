package com.usermanagement.user.application.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {
    private String email;
    private String role;
    private String password;
}
