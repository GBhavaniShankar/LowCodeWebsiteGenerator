package com.example.minimal.backend.service.mapper;

import com.example.minimal.backend.domain.Product;
import com.example.minimal.backend.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {}
