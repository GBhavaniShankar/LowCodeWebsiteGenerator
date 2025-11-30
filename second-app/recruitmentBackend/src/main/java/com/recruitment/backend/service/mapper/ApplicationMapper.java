package com.recruitment.backend.service.mapper;

import com.recruitment.backend.domain.Applicant;
import com.recruitment.backend.domain.Application;
import com.recruitment.backend.domain.ApplicationFeeCategory;
import com.recruitment.backend.service.dto.ApplicantDTO;
import com.recruitment.backend.service.dto.ApplicationDTO;
import com.recruitment.backend.service.dto.ApplicationFeeCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Application} and its DTO {@link ApplicationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApplicationMapper extends EntityMapper<ApplicationDTO, Application> {
    @Mapping(target = "applicant", source = "applicant", qualifiedByName = "applicantUsername")
    @Mapping(target = "feeCategory", source = "feeCategory", qualifiedByName = "applicationFeeCategoryId")
    ApplicationDTO toDto(Application s);

    @Named("applicantUsername")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    ApplicantDTO toDtoApplicantUsername(Applicant applicant);

    @Named("applicationFeeCategoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ApplicationFeeCategoryDTO toDtoApplicationFeeCategoryId(ApplicationFeeCategory applicationFeeCategory);
}
