package com.usermanagement.user.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name="profile")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean isActive;

    @PrePersist
    public void prePersist(){
        if(this.isActive==null){
            isActive=false;
        }
    }
    public ProfileEntity(){}
    public ProfileEntity(long id, String email, String password, LocalDateTime createdAt, LocalDateTime updatedAt,boolean isActive) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive=isActive;
    }
    public long getId() {
        return id;
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
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
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

    public static class ProfileBuilder{
        private long id;
        private String email;
        private String password;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isActive;

        public ProfileBuilder id(long id){this.id=id; return this;}
        public ProfileBuilder email(String email){this.email=email; return this;}
        public ProfileBuilder password(String password){this.password=password; return this;}
        public ProfileBuilder createdAt(LocalDateTime createdAt){this.createdAt=createdAt; return this;}
        public ProfileBuilder updatedAt(LocalDateTime updatedAt){this.updatedAt=updatedAt; return this;}
        public ProfileBuilder isActivre(Boolean isActive){this.isActive=isActive; return this;}

        public ProfileEntity build(){
            return new ProfileEntity(id,email,password, createdAt,updatedAt,isActive);
        }
    }
    public static ProfileBuilder builder(){
        return new ProfileBuilder();
    }
}
