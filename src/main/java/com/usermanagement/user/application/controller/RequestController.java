package com.usermanagement.user.application.controller;

import com.usermanagement.user.application.dto.RequestBrief;
import com.usermanagement.user.application.dto.RequestDTO;
import com.usermanagement.user.domain.entity.RequestEntity;
import com.usermanagement.user.domain.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    @Autowired
    RequestService requestService;

    @GetMapping
    public List<RequestBrief> getAllRequests() {
        return requestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public RequestDTO getRequestById(@PathVariable Long id) {
        return requestService.getRequestById(id);
    }

    @GetMapping("/count")
    public Long countRequests() {
        return requestService.countRequests();
    }

    @PostMapping
    public String sendRequest(@RequestBody RequestEntity request, @RequestParam Long profileId) {
        return requestService.sendRequest(request, profileId);
    }

    @PutMapping("/{id}/status")
    public String updateRequestStatus(@PathVariable Long id, @RequestParam(value = "statusId", required = true) Integer statusId) {
        return requestService.updateRequestStatus(id, statusId);
    }
}
