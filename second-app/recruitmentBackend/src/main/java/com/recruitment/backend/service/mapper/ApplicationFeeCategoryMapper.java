package com.recruitment.backend.service.mapper;

import com.recruitment.backend.domain.ApplicationFeeCategory;
import com.recruitment.backend.service.dto.ApplicationFeeCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ApplicationFeeCategory} and its DTO {@link ApplicationFeeCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ApplicationFeeCategoryMapper extends EntityMapper<ApplicationFeeCategoryDTO, ApplicationFeeCategory> {}
