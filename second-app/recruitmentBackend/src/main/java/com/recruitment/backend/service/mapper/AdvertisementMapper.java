package com.recruitment.backend.service.mapper;

import com.recruitment.backend.domain.Advertisement;
import com.recruitment.backend.service.dto.AdvertisementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Advertisement} and its DTO {@link AdvertisementDTO}.
 */
@Mapper(componentModel = "spring")
public interface AdvertisementMapper extends EntityMapper<AdvertisementDTO, Advertisement> {}
