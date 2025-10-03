package com.usermanagement.user.domain.service;

import com.usermanagement.user.application.dto.AuthDTO;
import com.usermanagement.user.application.dto.ProfileDTO;
import com.usermanagement.user.domain.entity.ProfileEntity;
import com.usermanagement.user.external.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository){
        this.profileRepository=profileRepository;
    }
    public ProfileDTO signup(ProfileDTO profileDTO) {
        ProfileEntity profileEntity= toEntity(profileDTO);
        profileEntity.setActivationToken(UUID.randomUUID().toString());
        profileEntity=profileRepository.save(profileEntity);
        return toDTO(profileEntity);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO){
         return ProfileEntity.builder()
                .id(profileDTO.getId())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .email(profileEntity.getEmail())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
}
