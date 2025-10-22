package com.usermanagement.user.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.user.application.dto.*;
import com.usermanagement.user.domain.entity.ArtistEntity;
import com.usermanagement.user.domain.entity.ProfileEntity;
import com.usermanagement.user.domain.entity.RoleEntity;
import com.usermanagement.user.domain.exception.ActivationFailedException;
import com.usermanagement.user.domain.exception.UserAlreadyExistException;
import com.usermanagement.user.external.repository.ArtistRepository;
import com.usermanagement.user.external.repository.ProfileRepository;
import com.usermanagement.user.utill.Jwtutil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.coobird.thumbnailator.Thumbnails;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
@Service
public class ProfileService {
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final Jwtutil jwtutil;
    private final ArtistRepository artistRepository;

    @Autowired
    private S3Client s3Client;
    private final String bucketName = "sonex2";

    public ProfileService(ProfileRepository profileRepository, MailService mailService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, Jwtutil jwtutil, ArtistRepository artistRepository) {
        this.profileRepository = profileRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtutil = jwtutil;
        this.artistRepository = artistRepository;
    }

    public ProfileDTO updateProfile(ProfileDTO profileDTO, MultipartFile profileImage, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtutil.extractUsername(token);

            System.out.println("email = " + username);
            Optional<ProfileEntity> optionalProfileEntity = profileRepository.findByEmail(username);
            if (optionalProfileEntity.isPresent()) {
                ProfileEntity profileEntity = optionalProfileEntity.get();
                profileEntity.setFirstName(profileDTO.getFirstName());
                profileEntity.setLastName(profileDTO.getLastName());
                profileEntity.setUpdatedAt(LocalDateTime.now());

                if (profileImage != null) {
                    System.out.println("userId = " + username);

                    try {
                        String uniqueFileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                        String s3Key = username + "/profile_images/" + uniqueFileName;

                        // ✅ Compress image in-memory
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Thumbnails.of(profileImage.getInputStream())
                                .size(800, 800)       // resize (max 800px)
                                .outputQuality(0.7)   // reduce quality for smaller file
                                .toOutputStream(outputStream);

                        byte[] compressedBytes = outputStream.toByteArray();

                        if(profileEntity.getProfileImageURL() != null && !profileEntity.getProfileImageURL().isEmpty()){
                            // Extract existing S3 key from URL
                            String existingUrl = profileEntity.getProfileImageURL();
                            String existingS3Key = existingUrl.substring(existingUrl.indexOf(".com/") + 5);

                            // Delete existing image from S3
                            s3Client.deleteObject(DeleteObjectRequest.builder()
                                    .bucket(bucketName)
                                    .key(existingS3Key)
                                    .build());
                            System.out.println("Deleted existing image from S3: " + existingS3Key);
                        }

                        // ✅ Upload compressed file to S3 (make public)
                        try (InputStream inputStream = new ByteArrayInputStream(compressedBytes)) {
                            s3Client.putObject(
                                    PutObjectRequest.builder()
                                            .bucket(bucketName)
                                            .key(s3Key)
                                            .contentType(profileImage.getContentType())
                                            .build(),
                                    RequestBody.fromInputStream(inputStream, compressedBytes.length)
                            );
                        }

                        // ✅ Store full public URL in DB
                        String publicUrl = "https://" + bucketName + ".s3.amazonaws.com/" + s3Key;
                        profileEntity.setProfileImageURL(publicUrl);

                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload artwork to S3", e);
                    }
                }

                profileRepository.save(profileEntity);
                return toDTO(profileEntity);
            } else {
                throw new UsernameNotFoundException("User with email " + username + " doesn't exists");
            }
        }
        else{
            throw new RuntimeException("Authorization header missing or invalid");
        }
    }


    public ProfileDTO signup(ProfileDTO profileDTO) throws UserAlreadyExistException {
        Optional<ProfileEntity> existingUser = profileRepository.findByEmail(profileDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistException(
                    "User with " + profileDTO.getEmail() + " already exists"
            );
        }
        ProfileEntity profileEntity = toEntity(profileDTO);
        profileEntity.setActivationToken(UUID.randomUUID().toString());
        profileEntity.setIsActive(false);
        profileEntity.setCreatedAt(LocalDateTime.now());
        profileEntity.setUpdatedAt(LocalDateTime.now());
        profileEntity.setRole(new RoleEntity(4, "user", null));
        profileEntity = profileRepository.save(profileEntity);

        //send activation email
        sendActivationEmailAsync(profileEntity);
        return toDTO(profileEntity);
    }

    @Async // Add this annotation
    public void sendActivationEmailAsync(ProfileEntity profileEntity) {
        try {
            String activationLink = "http://localhost:3000/activate?token=" + profileEntity.getActivationToken();
            String subject = "Activate your Email for Sonex";
            String body = "Click on the following link to activate your account: " + activationLink;
            mailService.sendEmail(profileEntity.getEmail(), subject, body);
        } catch (Exception e) {
            // Log the error but don't fail signup
            System.err.println("Failed to send activation email: " + e.getMessage());
        }
    }

    public String activate(String activationToken) {
        Optional<ProfileEntity> profileEntity = profileRepository.findByActivationToken(activationToken);
        if (profileEntity.isEmpty()) {
            throw new ActivationFailedException("Activation Failed due to Invalid Token ");
        } else {
            ProfileEntity profile = profileEntity.get();
            profile.setIsActive(true);
            profileRepository.save(profile);
            return "Activation Success";
        }
    }

    public boolean isActive(String email) {
        Optional<ProfileEntity> currentProfile = profileRepository.findByEmail(email);
        if (currentProfile.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + email + " doesn't exists");
        } else {
            return Boolean.TRUE.equals(currentProfile.get().getIsActive());
        }
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
            String token = jwtutil.generateToken(authDto.getEmail(), authDto.getRole());
            return Map.of(
                    "Access_Token", token,
                    "User", getPublicProfile(authDto.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }
        return toDTO(currentUser);
    }

    public String changePassword(AuthDTO authDTO) {
        Optional<ProfileEntity> optionalProfileEntity = profileRepository.findByEmail(authDTO.getEmail());
        if (optionalProfileEntity.isPresent()) {
            ProfileEntity profile = optionalProfileEntity.get();
            profile.setPassword(passwordEncoder.encode(authDTO.getPassword()));
            profileRepository.save(profile);
            return "Password changed successfully!";
        } else {
            throw new UsernameNotFoundException("User with email " + authDTO.getEmail() + " doesn't exists");
        }
    }

    public ProfileDTO changeEmail(EmailChangeDTO emailChangeDTO) {
        Optional<ProfileEntity> profile = profileRepository.findByEmail(emailChangeDTO.getOldEmail());
        if (profile.isPresent()) {
            ProfileEntity profileEntity = profile.get();
            profileEntity.setEmail(emailChangeDTO.getNewEmail());
            profileEntity.setIsActive(Boolean.FALSE);
            profileRepository.save(profileEntity);

            //send activation email
            String activationLink = "http://localhost:3020/sonex/v1/auth/activate?token=" + profileEntity.getActivationToken();
            String subject = "Activate your Email for Sonex";
            String body = "Click on the following link to activate your account: " + activationLink;
            mailService.sendEmail(profileEntity.getEmail(), subject, body);
            return toDTO(profileEntity);

        } else {
            throw new UsernameNotFoundException("User with email " + emailChangeDTO.getOldEmail() + " doesn't exists");
        }
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
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

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .profileId(profileEntity.getProfileId())
                .email(profileEntity.getEmail())
                .profileImageURL(profileEntity.getProfileImageURL())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .firstName(profileEntity.getFirstName())
                .lastName(profileEntity.getLastName())
                .role(profileEntity.getRole() != null ? new RoleDTO(profileEntity.getRole().getRoleId(), profileEntity.getRole().getRoleName()) : null)
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

    public List<ProfileAdminDTO> getAllProfiles() {
        List<ProfileEntity> entities = profileRepository.findAll();
        return entities.stream()
                .map(this::toAdminDTO)
                .collect(Collectors.toList());
    }

    private ProfileAdminDTO toAdminDTO(ProfileEntity entity) {
        if (entity == null) return null;
        ProfileAdminDTO dto = new ProfileAdminDTO();
        dto.setProfileId(entity.getProfileId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setIsActive(entity.getIsActive());
        dto.setProfileImageURL(entity.getProfileImageURL());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setRole(new RoleDTO(
                entity.getRole().getRoleId(),
                entity.getRole().getRoleName()
        ));
        return dto;
    }

}
