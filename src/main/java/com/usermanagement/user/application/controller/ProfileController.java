package com.usermanagement.user.application.controller;

import com.usermanagement.user.application.dto.AuthDTO;
import com.usermanagement.user.application.dto.ProfileDTO;

import com.usermanagement.user.domain.exception.UserAlreadyExistException;
import com.usermanagement.user.domain.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

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

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO){
        try{
            if(!profileService.isActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Account is not active, Please activate your account first"
                ));
            }
            Map<String,Object> response= profileService.authenticateAndGenerateToken(authDTO) ;
            return ResponseEntity.ok(response);
        }
        catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", ex.getMessage()));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "Message", e.getMessage()
            ));
        }
    }
}
