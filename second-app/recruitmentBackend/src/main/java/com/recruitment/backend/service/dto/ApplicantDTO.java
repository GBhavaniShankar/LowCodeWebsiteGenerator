package com.recruitment.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.recruitment.backend.domain.Applicant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApplicantDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 5)
    private String username;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+.[^@\\s]+$")
    private String email;

    private String passwordHash;

    private String firstName;

    private String lastName;

    private Boolean isAccountActivated;

    private String authorities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getIsAccountActivated() {
        return isAccountActivated;
    }

    public void setIsAccountActivated(Boolean isAccountActivated) {
        this.isAccountActivated = isAccountActivated;
    }

    public String getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicantDTO)) {
            return false;
        }

        ApplicantDTO applicantDTO = (ApplicantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, applicantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicantDTO{" +
            "id=" + getId() +
            ", username='" + getUsername() + "'" +
            ", email='" + getEmail() + "'" +
            ", passwordHash='" + getPasswordHash() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", isAccountActivated='" + getIsAccountActivated() + "'" +
            ", authorities='" + getAuthorities() + "'" +
            "}";
    }
}
