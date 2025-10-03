package com.usermanagement.user.application.controller;

import com.usermanagement.user.application.dto.AuthDTO;
import com.usermanagement.user.application.dto.ProfileDTO;

import com.usermanagement.user.domain.exception.UserAlreadyExistException;
import com.usermanagement.user.domain.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sonex/v1/auth")
public class ProfileController {
    private final ProfileService profileService;
    public ProfileController(ProfileService profileService){
        this.profileService=profileService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ProfileDTO> signup(@RequestBody ProfileDTO profileDTO){
            ProfileDTO profileDTO1= profileService.signup(profileDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(profileDTO1);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam String token){
        String msg= profileService.activate(token);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(msg);
    }
}
