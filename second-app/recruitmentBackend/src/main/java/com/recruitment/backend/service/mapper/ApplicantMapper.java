package com.recruitment.backend.service.mapper;

import com.recruitment.backend.domain.Applicant;
import com.recruitment.backend.service.dto.ApplicantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Applicant} and its DTO {@link ApplicantDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApplicantMapper extends EntityMapper<ApplicantDTO, Applicant> {}
