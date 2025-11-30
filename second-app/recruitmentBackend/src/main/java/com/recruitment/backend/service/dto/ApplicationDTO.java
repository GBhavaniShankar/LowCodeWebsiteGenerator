package com.recruitment.backend.service.dto;

import com.recruitment.backend.domain.enumeration.ApplicationStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.recruitment.backend.domain.Application} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApplicationDTO implements Serializable {

    private Long id;

    @NotNull
    private String uniqueNumber;

    private Instant submissionDate;

    @NotNull
    private ApplicationStatus status;

    private Boolean paymentSuccessful;

    private ApplicantDTO applicant;

    private ApplicationFeeCategoryDTO feeCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueNumber() {
        return uniqueNumber;
    }

    public void setUniqueNumber(String uniqueNumber) {
        this.uniqueNumber = uniqueNumber;
    }

    public Instant getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Instant submissionDate) {
        this.submissionDate = submissionDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Boolean getPaymentSuccessful() {
        return paymentSuccessful;
    }

    public void setPaymentSuccessful(Boolean paymentSuccessful) {
        this.paymentSuccessful = paymentSuccessful;
    }

    public ApplicantDTO getApplicant() {
        return applicant;
    }

    public void setApplicant(ApplicantDTO applicant) {
        this.applicant = applicant;
    }

    public ApplicationFeeCategoryDTO getFeeCategory() {
        return feeCategory;
    }

    public void setFeeCategory(ApplicationFeeCategoryDTO feeCategory) {
        this.feeCategory = feeCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationDTO)) {
            return false;
        }

        ApplicationDTO applicationDTO = (ApplicationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, applicationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationDTO{" +
            "id=" + getId() +
            ", uniqueNumber='" + getUniqueNumber() + "'" +
            ", submissionDate='" + getSubmissionDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", paymentSuccessful='" + getPaymentSuccessful() + "'" +
            ", applicant=" + getApplicant() +
            ", feeCategory=" + getFeeCategory() +
            "}";
    }
}
