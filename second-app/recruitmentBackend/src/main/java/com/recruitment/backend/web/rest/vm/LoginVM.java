package com.recruitment.backend.web.rest.vm;

import jakarta.validation.constraints.NotBlank;

/**
 * View Model object for storing a user's credentials.
 */
public class LoginVM {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private boolean rememberMe = false;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
