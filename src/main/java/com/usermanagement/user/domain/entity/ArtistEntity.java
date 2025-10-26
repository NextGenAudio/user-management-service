package com.usermanagement.user.domain.entity;

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
@Entity(name = "artists")
public class ArtistEntity {
    @Id
    @Column(name="artist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artistId;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private ProfileEntity profile;

    @Column(name = "artist_name")
    private String artistName;

    @Column(name="instagram")
    private String instagram;

    @Column(name="spotify")
    private String spotify;

    @Column(name="artist_image_url")
    private String artistImageURL;

    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name="youtube")
    private String youtube;

    @Column(name="artist_bio", columnDefinition = "TEXT")
    private String artistBio;

    @Column(name="genre")
    private String genre;

    @Column(name="website")
    private String website;
}
