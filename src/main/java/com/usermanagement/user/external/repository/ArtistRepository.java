package com.usermanagement.user.external.repository;

import com.usermanagement.user.domain.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {

    ArtistEntity findByProfileProfileId(Long profileProfileId);
}
