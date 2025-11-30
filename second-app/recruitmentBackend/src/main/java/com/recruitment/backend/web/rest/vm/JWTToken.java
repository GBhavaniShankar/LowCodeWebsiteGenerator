package com.recruitment.backend.web.rest.vm;

/**
 * JWT token response.
 */
public class JWTToken {

    private String idToken;

    public JWTToken() {}

    public JWTToken(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
