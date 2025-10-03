package com.usermanagement.user.domain.service;

import com.usermanagement.user.domain.entity.ProfileEntity;
import com.usermanagement.user.external.repository.ProfileRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AppUserDetailService implements UserDetailsService {
    private final ProfileRepository profileRepository;
    public AppUserDetailService(ProfileRepository profileRepository){
        this.profileRepository=profileRepository;
    }

    public UserDetails loadUserByUsername(String email) {
        ProfileEntity currentProfile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find User with email :" + email));
        return User.builder()
                .username(currentProfile.getEmail())
                .password(currentProfile.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
