package com.recruitment.backend.service.dto;

import com.recruitment.backend.domain.enumeration.NotificationSource;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.recruitment.backend.domain.Notification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @Lob
    private String message;

    private Boolean isRead;

    @NotNull
    private NotificationSource generatedBy;

    private ApplicantDTO recipient;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public NotificationSource getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(NotificationSource generatedBy) {
        this.generatedBy = generatedBy;
    }

    public ApplicantDTO getRecipient() {
        return recipient;
    }

    public void setRecipient(ApplicantDTO recipient) {
        this.recipient = recipient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationDTO)) {
            return false;
        }

        NotificationDTO notificationDTO = (NotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", message='" + getMessage() + "'" +
            ", isRead='" + getIsRead() + "'" +
            ", generatedBy='" + getGeneratedBy() + "'" +
            ", recipient=" + getRecipient() +
            "}";
    }
}
