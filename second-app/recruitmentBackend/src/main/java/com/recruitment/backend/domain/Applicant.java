package com.recruitment.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Applicant.
 */
@Entity
@Table(name = "applicant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Applicant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 5)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+.[^@\\s]+$")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "is_account_activated")
    private Boolean isAccountActivated;

    @Column(name = "authorities")
    private String authorities;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Applicant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public Applicant username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public Applicant email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public Applicant passwordHash(String passwordHash) {
        this.setPasswordHash(passwordHash);
        return this;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Applicant firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Applicant lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getIsAccountActivated() {
        return this.isAccountActivated;
    }

    public Applicant isAccountActivated(Boolean isAccountActivated) {
        this.setIsAccountActivated(isAccountActivated);
        return this;
    }

    public void setIsAccountActivated(Boolean isAccountActivated) {
        this.isAccountActivated = isAccountActivated;
    }

    public String getAuthorities() {
        return this.authorities;
    }

    public Applicant authorities(String authorities) {
        this.setAuthorities(authorities);
        return this;
    }

    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Applicant)) {
            return false;
        }
        return getId() != null && getId().equals(((Applicant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Applicant{" +
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
