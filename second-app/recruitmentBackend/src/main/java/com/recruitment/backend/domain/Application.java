package com.recruitment.backend.domain;

import com.recruitment.backend.domain.enumeration.ApplicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Application.
 */
@Entity
@Table(name = "application")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "unique_number", nullable = false, unique = true)
    private String uniqueNumber;

    @Column(name = "submission_date")
    private Instant submissionDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status;

    @Column(name = "payment_successful")
    private Boolean paymentSuccessful;

    @ManyToOne(fetch = FetchType.LAZY)
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationFeeCategory feeCategory;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Application id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueNumber() {
        return this.uniqueNumber;
    }

    public Application uniqueNumber(String uniqueNumber) {
        this.setUniqueNumber(uniqueNumber);
        return this;
    }

    public void setUniqueNumber(String uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    public Instant getSubmissionDate() {
        return this.submissionDate;
    }

    public Application submissionDate(Instant submissionDate) {
        this.setSubmissionDate(submissionDate);
        return this;
    }

    public void setSubmissionDate(Instant submissionDate) {
        this.submissionDate = submissionDate;
    }

    public ApplicationStatus getStatus() {
        return this.status;
    }

    public Application status(ApplicationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Boolean getPaymentSuccessful() {
        return this.paymentSuccessful;
    }

    public Application paymentSuccessful(Boolean paymentSuccessful) {
        this.setPaymentSuccessful(paymentSuccessful);
        return this;
    }

    public void setPaymentSuccessful(Boolean paymentSuccessful) {
        this.paymentSuccessful = paymentSuccessful;
    }

    public Applicant getApplicant() {
        return this.applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Application applicant(Applicant applicant) {
        this.setApplicant(applicant);
        return this;
    }

    public ApplicationFeeCategory getFeeCategory() {
        return this.feeCategory;
    }

    public void setFeeCategory(ApplicationFeeCategory applicationFeeCategory) {
        this.feeCategory = applicationFeeCategory;
    }

    public Application feeCategory(ApplicationFeeCategory applicationFeeCategory) {
        this.setFeeCategory(applicationFeeCategory);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Application)) {
            return false;
        }
        return getId() != null && getId().equals(((Application) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Application{" +
            "id=" + getId() +
            ", uniqueNumber='" + getUniqueNumber() + "'" +
            ", submissionDate='" + getSubmissionDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", paymentSuccessful='" + getPaymentSuccessful() + "'" +
            "}";
    }
}
