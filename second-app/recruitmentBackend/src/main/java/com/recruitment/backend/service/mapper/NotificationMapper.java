package com.recruitment.backend.service.mapper;

import com.recruitment.backend.domain.Applicant;
import com.recruitment.backend.domain.Notification;
import com.recruitment.backend.service.dto.ApplicantDTO;
import com.recruitment.backend.service.dto.NotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "recipient", source = "recipient", qualifiedByName = "applicantUsername")
    NotificationDTO toDto(Notification s);

    @Named("applicantUsername")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    ApplicantDTO toDtoApplicantUsername(Applicant applicant);
}
