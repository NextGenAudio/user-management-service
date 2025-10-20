package com.usermanagement.user.domain.service;

import com.usermanagement.user.application.dto.*;
import com.usermanagement.user.domain.entity.ProfileEntity;
import com.usermanagement.user.domain.exception.ActivationFailedException;
import com.usermanagement.user.domain.exception.UserAlreadyExistException;
import com.usermanagement.user.external.repository.ProfileRepository;
import com.usermanagement.user.utill.Jwtutil;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfileService {
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final Jwtutil jwtutil;

    public ProfileService(ProfileRepository profileRepository, MailService mailService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, Jwtutil jwtutil){
        this.profileRepository=profileRepository;
        this.mailService=mailService;
        this.passwordEncoder=passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtutil = jwtutil;
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
        sendActivationEmailAsync(profileEntity);
        return toDTO(profileEntity);
    }

    @Async // Add this annotation
    public void sendActivationEmailAsync(ProfileEntity profileEntity) {
        try {
            String activationLink = "http://localhost:3020/sonex/v1/auth/activate?token=" + profileEntity.getActivationToken();
            String subject = "Activate your Email for Sonex";
            String body = "Click on the following link to activate your account: " + activationLink;
            mailService.sendEmail(profileEntity.getEmail(), subject, body);
        } catch (Exception e) {
            // Log the error but don't fail signup
            System.err.println("Failed to send activation email: " + e.getMessage());
        }
    }

    public String activate (String activationToken){
        Optional<ProfileEntity> profileEntity=profileRepository.findByActivationToken(activationToken);
        if(profileEntity.isEmpty()){
            throw new ActivationFailedException("Activation Failed due to Invalid Token ");
        }else {
            ProfileEntity profile=profileEntity.get();
            profile.setIsActive(true);
            profileRepository.save(profile);
            return "Activation Success";
        }
    }

    public boolean isActive(String email){
        Optional <ProfileEntity> currentProfile= profileRepository.findByEmail(email);
        if(currentProfile.isEmpty()){
            throw new UsernameNotFoundException("User with email "+ email+ " doesn't exists");
        }else{
            return Boolean.TRUE.equals(currentProfile.get().getIsActive());
        }
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDto){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(),authDto.getPassword()));
            String token= jwtutil.generateToken(authDto.getEmail());
            return Map.of(
                    "Access_Token",token,
                    "User",getPublicProfile(authDto.getEmail())
            );
        }catch (Exception e){
            throw new RuntimeException("Invalid email or password");
        }
    }
    public ProfileEntity getCurrentProfile(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: "+authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser = null;
        if(email==null){
            currentUser= getCurrentProfile();
        }else{
            currentUser= profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: "+ email));
        }
        return toDTO(currentUser);
    }

    public String changePassword(AuthDTO authDTO){
        Optional<ProfileEntity> optionalProfileEntity=profileRepository.findByEmail(authDTO.getEmail());
        if(optionalProfileEntity.isPresent()){
            ProfileEntity profile=optionalProfileEntity.get();
            profile.setPassword(passwordEncoder.encode(authDTO.getPassword()));
            profileRepository.save(profile);
            return "Password changed successfully!";
        }else{
            throw new UsernameNotFoundException("User with email "+authDTO.getEmail()+" doesn't exists");
        }
    }

    public ProfileDTO changeEmail(EmailChangeDTO emailChangeDTO){
        Optional <ProfileEntity> profile=profileRepository.findByEmail(emailChangeDTO.getOldEmail());
        if (profile.isPresent()){
            ProfileEntity profileEntity=profile.get();
            profileEntity.setEmail(emailChangeDTO.getNewEmail());
            profileEntity.setIsActive(Boolean.FALSE);
            profileRepository.save(profileEntity);

            //send activation email
            String activationLink ="http://localhost:3020/sonex/v1/auth/activate?token=" + profileEntity.getActivationToken();
            String subject= "Activate your Email for Sonex";
            String body= "Click on the following link to activate your account: " + activationLink;
            mailService.sendEmail(profileEntity.getEmail(),subject,body);
            return toDTO(profileEntity);

        }else{
            throw new UsernameNotFoundException("User with email "+emailChangeDTO.getOldEmail()+" doesn't exists");
        }
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO){
         return ProfileEntity.builder()
                .profileId(profileDTO.getProfileId())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                 .profileImageURL(profileDTO.getProfileImageURL())
                 .firstName(profileDTO.getFirstName())
                 .lastName(profileDTO.getLastName())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .profileId(profileEntity.getProfileId())
                .email(profileEntity.getEmail())
                .profileImageURL(profileEntity.getProfileImageURL())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .firstName(profileEntity.getFirstName())
                .lastName(profileEntity.getLastName())
                .build();
    }

    public List<ProfileSearchDTO> searchProfile(String search) {
        List<ProfileEntity> entities = profileRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search, search, search
        );
        return entities.stream()
                .map(this::toSearchDTO)
                .collect(Collectors.toList());
    }

    private ProfileSearchDTO toSearchDTO(ProfileEntity entity) {
        if (entity == null) return null;
        ProfileSearchDTO dto = new ProfileSearchDTO();
        dto.setProfileId(entity.getProfileId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setIsActive(entity.getIsActive());
        dto.setProfileImageURL(entity.getProfileImageURL());
        dto.setCreatedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toLocalDate() : null);
        return dto;
    }

    public List<ProfileAdminDTO> getAllProfiles(){
        List<ProfileEntity> entities= profileRepository.findAll();
        return entities.stream()
                .map(this::toAdminDTO)
                .collect(Collectors.toList());
    }

    private ProfileAdminDTO toAdminDTO(ProfileEntity entity){
        if(entity==null) return null;
        ProfileAdminDTO dto=new ProfileAdminDTO();
        dto.setProfileId(entity.getProfileId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setIsActive(entity.getIsActive());
        dto.setProfileImageURL(entity.getProfileImageURL());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

}
