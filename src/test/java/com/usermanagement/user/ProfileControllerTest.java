package com.usermanagement.user;

import com.usermanagement.user.application.controller.ProfileController;
import com.usermanagement.user.application.dto.AuthDTO;
import com.usermanagement.user.application.dto.ProfileDTO;
import com.usermanagement.user.domain.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@SpringBootTest
public class ProfileControllerTest {
    @Mock
    private ProfileService profileService;
    private ProfileController profileController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        profileController = new ProfileController(profileService);
    }

    @Test
    void signup_Success(){
        ProfileDTO inputDto=ProfileDTO.builder()
                .email("test@test.com")
                .password("password123")
                .build();
        ProfileDTO expectedDto = ProfileDTO.builder()
                .id(1L)
                .email("test@test.com")
                .createdAt(LocalDateTime.now())
                .build();
        when(profileService.signup(any(ProfileDTO.class))).thenReturn(expectedDto);

        ResponseEntity<ProfileDTO> response = profileController.signup(inputDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedDto.getEmail(), response.getBody().getEmail());
    }

    @Test
    void login_Success() {
        AuthDTO authDTO = new AuthDTO("test@test.com", "password");
        Map<String, Object> expectedResponse = Map.of("token", "jwt-token");

        when(profileService.isActive("test@test.com")).thenReturn(true);
        when(profileService.authenticateAndGenerateToken(any(AuthDTO.class))).thenReturn(expectedResponse);

        ResponseEntity<Map<String, Object>> response = profileController.login(authDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void login_InactiveAccount() {
        AuthDTO authDTO = new AuthDTO("test@test.com", "password");
        when(profileService.isActive("test@test.com")).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = profileController.login(authDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void login_InvalidCredentials() {
        AuthDTO authDTO = new AuthDTO("test@test.com", "password");
        when(profileService.isActive("test@test.com")).thenReturn(true);
        when(profileService.authenticateAndGenerateToken(any(AuthDTO.class)))
                .thenThrow(new UsernameNotFoundException("Invalid credentials"));

        ResponseEntity<Map<String, Object>> response = profileController.login(authDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
