package com.usermanagement.user.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestBrief {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private StatusDTO status;
    private LocalDateTime createdAt;
}
