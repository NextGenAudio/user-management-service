package com.usermanagement.user.application.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistSearchDTO {
    private Long artistId;
    private String artistName;
    private ProfileSearchDTO profile;
}
