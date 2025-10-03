package com.usermanagement.user.domain.service;

import com.usermanagement.user.application.dto.AuthDTO;
import com.usermanagement.user.application.dto.ProfileDTO;
import com.usermanagement.user.domain.entity.ProfileEntity;
import com.usermanagement.user.domain.exception.UserAlreadyExistException;
import com.usermanagement.user.external.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final MailService mailService;

    public ProfileService(ProfileRepository profileRepository,MailService mailService){
        this.profileRepository=profileRepository;
        this.mailService=mailService;
    }
    public ProfileDTO signup(ProfileDTO profileDTO) throws UserAlreadyExistException {
        Optional<ProfileEntity> existingUser= profileRepository.findByEmail(profileDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistException(
                    "User with " + profileDTO.getEmail() + " already exists"
            );
        }
        ProfileEntity profileEntity= toEntity(profileDTO);
        profileEntity.setActivationToken(UUID.randomUUID().toString());
        profileEntity=profileRepository.save(profileEntity);

        //send activation email
        String activationLink ="http://localhost:3020/profile/activate?token=" + profileEntity.getActivationToken();
        String subject= "Activate your Email for Money Manager";
        String body= "Click on the following link to activate your account: " + activationLink;
        mailService.sendEmail(profileEntity.getEmail(),subject,body);
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
