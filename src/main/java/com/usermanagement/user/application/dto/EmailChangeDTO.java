package com.usermanagement.user.application.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailChangeDTO {
    String oldEmail;
    String newEmail;
    String password;

    public EmailChangeDTO(String oldEmail, String newEmail, String password) {
        this.oldEmail = oldEmail;
        this.newEmail = newEmail;
        this.password = password;
    }


}
