package com.recruitment.backend.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Config.
 */
@Entity
@Table(name = "config")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "portal_active")
    private Boolean portalActive;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "sample_form_url")
    private String sampleFormUrl;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Config id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPortalActive() {
        return this.portalActive;
    }

    public Config portalActive(Boolean portalActive) {
        this.setPortalActive(portalActive);
        return this;
    }

    public void setPortalActive(Boolean portalActive) {
        this.portalActive = portalActive;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Config startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Config endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getSampleFormUrl() {
        return this.sampleFormUrl;
    }

    public Config sampleFormUrl(String sampleFormUrl) {
        this.setSampleFormUrl(sampleFormUrl);
        return this;
    }

    public void setSampleFormUrl(String sampleFormUrl) {
        this.sampleFormUrl = sampleFormUrl;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Config)) {
            return false;
        }
        return getId() != null && getId().equals(((Config) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Config{" +
            "id=" + getId() +
            ", portalActive='" + getPortalActive() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", sampleFormUrl='" + getSampleFormUrl() + "'" +
            "}";
    }
}
