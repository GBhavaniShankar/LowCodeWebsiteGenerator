package com.recruitment.backend.service.mapper;

import com.recruitment.backend.domain.Config;
import com.recruitment.backend.service.dto.ConfigDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Config} and its DTO {@link ConfigDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConfigMapper extends EntityMapper<ConfigDTO, Config> {}
