package com.usermanagement.user.external.repository;

import com.usermanagement.user.domain.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {

    ArtistEntity findByProfileProfileId(Long profileProfileId);
    List<ArtistEntity> findByArtistNameContainingIgnoreCase(String artistName);

}
