package com.usermanagement.user.domain.service;

import com.usermanagement.user.application.dto.RequestBrief;
import com.usermanagement.user.application.dto.StatusDTO;
import com.usermanagement.user.domain.entity.RequestEntity;
import com.usermanagement.user.domain.entity.Status;
import com.usermanagement.user.external.repository.ProfileRepository;
import com.usermanagement.user.external.repository.RequestRepository;
import com.usermanagement.user.external.repository.StatusRepository;
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
    @Autowired
    public RequestService(RequestRepository requestRepository, StatusRepository statusRepository, ProfileRepository profileRepository) {
        this.requestRepository = requestRepository;
        this.statusRepository = statusRepository;
        this.profileRepository = profileRepository;
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

    public RequestEntity getRequestById(Long id) {
        return requestRepository.findById(id).orElse(null);
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
        requestEntity.setStatus(optionalStatus.get());
        requestRepository.save(requestEntity);

        if(statusId == 2) {
            optionalRequest.
            System.out.println("Request approved. Notify user: " + requestEntity.getEmail());
        } else if(statusId == 3) { // Assuming statusId 4 corresponds to "Rejected"
            // Here you can add logic to notify the user about rejection
            System.out.println("Request rejected. Notify user: " + requestEntity.getEmail());
        }
        return "Request status updated successfully";
    }
}
