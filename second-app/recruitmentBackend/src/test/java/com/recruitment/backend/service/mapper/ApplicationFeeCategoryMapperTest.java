package com.recruitment.backend.service.mapper;

import static com.recruitment.backend.domain.ApplicationFeeCategoryAsserts.*;
import static com.recruitment.backend.domain.ApplicationFeeCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationFeeCategoryMapperTest {

    private ApplicationFeeCategoryMapper applicationFeeCategoryMapper;

    @BeforeEach
    void setUp() {
        applicationFeeCategoryMapper = new ApplicationFeeCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getApplicationFeeCategorySample1();
        var actual = applicationFeeCategoryMapper.toEntity(applicationFeeCategoryMapper.toDto(expected));
        assertApplicationFeeCategoryAllPropertiesEquals(expected, actual);
    }
}
