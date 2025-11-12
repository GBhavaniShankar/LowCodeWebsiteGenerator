package com.recruitment.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.recruitment.backend.domain.ApplicationFeeCategory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ApplicationFeeCategoryDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal fee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationFeeCategoryDTO)) {
            return false;
        }

        ApplicationFeeCategoryDTO applicationFeeCategoryDTO = (ApplicationFeeCategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, applicationFeeCategoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApplicationFeeCategoryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", fee=" + getFee() +
            "}";
    }
}
