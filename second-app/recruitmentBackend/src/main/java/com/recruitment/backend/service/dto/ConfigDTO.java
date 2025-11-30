package com.recruitment.backend.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.recruitment.backend.domain.Config} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConfigDTO implements Serializable {

    private Long id;

    private Boolean portalActive;

    private Instant startDate;

    private Instant endDate;

    private String sampleFormUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPortalActive() {
        return portalActive;
    }

    public void setPortalActive(Boolean portalActive) {
        this.portalActive = portalActive;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getSampleFormUrl() {
        return sampleFormUrl;
    }

    public void setSampleFormUrl(String sampleFormUrl) {
        this.sampleFormUrl = sampleFormUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigDTO)) {
            return false;
        }

        ConfigDTO configDTO = (ConfigDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, configDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConfigDTO{" +
            "id=" + getId() +
            ", portalActive='" + getPortalActive() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", sampleFormUrl='" + getSampleFormUrl() + "'" +
            "}";
    }
}
