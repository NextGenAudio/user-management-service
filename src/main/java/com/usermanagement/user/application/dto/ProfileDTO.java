package com.usermanagement.user.application.dto;

import java.time.LocalDateTime;

public class ProfileDTO {
    private long id;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String activationToken;
    private String profileImageURL;
    private String name;

    public ProfileDTO(long id,  String email, LocalDateTime createdAt, LocalDateTime updatedAt,String activationToken,String profileImageURL, String name) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.activationToken=activationToken;
        this.profileImageURL=profileImageURL;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public long getId() {
        return id;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public static class ProfileDtoBuilder{
        private long id;
        private String email;
        private String password;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String activationToken;
        private String profileImageURL;
        private String name;
        public ProfileDtoBuilder id(Long id){this.id=id; return this;}
        public ProfileDtoBuilder email(String email){this.email=email; return this;}
        public ProfileDtoBuilder password(String password){this.password=password; return this;}
        public ProfileDtoBuilder createdAt (LocalDateTime createdAt){this.createdAt=createdAt; return this;}
        public ProfileDtoBuilder updatedAt (LocalDateTime updatedAt){this.updatedAt=updatedAt; return  this;}
        public ProfileDtoBuilder activationToken(String activationToken){this.activationToken=activationToken; return this;}
        public ProfileDtoBuilder profileImageURL(String profileImageURL){this.profileImageURL=profileImageURL; return this;}
        public ProfileDtoBuilder name(String name){this.name=name; return this;}

        public ProfileDTO build(){
            return new ProfileDTO(id,email,createdAt,updatedAt,activationToken, profileImageURL,name);
        }
    }
    public static ProfileDtoBuilder builder(){
        return new ProfileDtoBuilder();
    }
}