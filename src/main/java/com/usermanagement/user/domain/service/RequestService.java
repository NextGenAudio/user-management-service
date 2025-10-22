package com.usermanagement.user.domain.service;

import com.usermanagement.user.application.dto.*;
import com.usermanagement.user.domain.entity.*;
import com.usermanagement.user.external.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final StatusRepository statusRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository, StatusRepository statusRepository, ProfileRepository profileRepository, RoleRepository roleRepository, ArtistRepository artistRepository) {
        this.requestRepository = requestRepository;
        this.statusRepository = statusRepository;
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.artistRepository = artistRepository;
    }

    public List<RequestBrief> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(this::toBrief)
                .toList();
    }

    public RequestBrief toBrief(RequestEntity entity) {
        return RequestBrief.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .status(new StatusDTO(entity.getStatus().getId(), entity.getStatus().getName()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public RequestDTO toDTO(RequestEntity entity) {
        if (entity == null) return null;
        return new RequestDTO(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            toProfileAdminDTO(entity.getProfile()),
            entity.getStageName(),
            entity.getPrimaryGenre(),
            entity.getArtistBio(),
            entity.getWebsite(),
            entity.getInstagram(),
            entity.getYoutube(),
            entity.getSpotify(),
            entity.getSampleWork(),
            entity.getCreatedAt(),
            entity.getStatus()
        );
    }

    public ProfileAdminDTO toProfileAdminDTO(ProfileEntity profile) {
        if (profile == null) return null;
        return ProfileAdminDTO.builder()
            .profileId(profile.getProfileId())
            .firstName(profile.getFirstName())
            .lastName(profile.getLastName())
            .email(profile.getEmail())
            .createdAt(profile.getCreatedAt())
            .updatedAt(profile.getUpdatedAt())
            .isActive(profile.getIsActive())
            .profileImageURL(profile.getProfileImageURL())
            .role(RoleDTO.builder()
                .roleId(profile.getRole().getRoleId())
                .roleName(profile.getRole().getRoleName())
                .build())
            .build();
    }

    public RequestDTO getRequestById(Long id) {
        return requestRepository.findById(id)
            .map(this::toDTO)
            .orElse(null);
    }

    public String sendRequest(RequestEntity requestEntity) {
        Status defaultStatus = statusRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Default status not found"));
        System.out.println(requestEntity.toString());
        requestEntity.setStatus(defaultStatus);
        requestEntity.setCreatedAt(LocalDateTime.now());
        requestRepository.save(requestEntity);

        return "Request sent successfully";
    }

    public Long countRequests(){
        return requestRepository.count();
    }

    public String updateRequestStatus(Long requestId, Integer statusId) {
        Optional<RequestEntity> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            return "Request not found";
        }
        Optional<Status> optionalStatus = statusRepository.findById(statusId);
        if (optionalStatus.isEmpty()) {
            return "Status not found";
        }
        RequestEntity requestEntity = optionalRequest.get();

        if (statusId == 2) {
            ProfileEntity profile = profileRepository.findById(requestEntity.getProfile().getProfileId())
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            RoleEntity artistRole = roleRepository.findById(3)
                    .orElseThrow(() -> new RuntimeException("Artist role not found"));

            // Update profile role
            profile.setRole(artistRole);
            profileRepository.save(profile);

            // Create a new ArtistEntity and save it
            ArtistEntity artist = new ArtistEntity();
            artist.setProfile(profile);
            artist.setArtistName(requestEntity.getStageName());
            artist.setInstagram(requestEntity.getInstagram());
            artist.setSpotify(requestEntity.getSpotify());
            artist.setWebsite(requestEntity.getWebsite());
            artist.setYoutube(requestEntity.getYoutube());
            artist.setArtistBio(requestEntity.getArtistBio());
            artist.setGenre(requestEntity.getPrimaryGenre());
            artist.setCreatedAt(LocalDateTime.now());
            artistRepository.save(artist);

            System.out.println("Artist created successfully for profile: " + profile.getProfileId());
        } else if (statusId == 3) {
            System.out.println("Request rejected. Notify user: " + requestEntity.getEmail());
        }
        requestEntity.setStatus(optionalStatus.get());
        requestRepository.save(requestEntity);

        return "Request status updated successfully";

    }
}
