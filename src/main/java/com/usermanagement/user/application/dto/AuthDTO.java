package com.usermanagement.user.application.dto;

public class AuthDTO {
    private String email;
    private String password;
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthDTO(String email, String password, String token) {
        this.email = email;
        this.password = password;
        this.token = token;
    }

    public static class AuthDTOBuilder{
        private String email;
        private String password;
        private String token;
        public AuthDTOBuilder email(String email){this.email=email; return this;}
        public AuthDTOBuilder password(String password){this.password=password; return this;}
        public AuthDTOBuilder token(String token){this.token=token; return this;}

        public AuthDTO build(){
            return new AuthDTO(this.email,this.password,this.token);
        }
    }

    public static AuthDTOBuilder builder(){
        return new AuthDTOBuilder();
    }

}
