package com.recruitment.backend.service.mapper;

import static com.recruitment.backend.domain.ConfigAsserts.*;
import static com.recruitment.backend.domain.ConfigTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigMapperTest {

    private ConfigMapper configMapper;

    @BeforeEach
    void setUp() {
        configMapper = new ConfigMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getConfigSample1();
        var actual = configMapper.toEntity(configMapper.toDto(expected));
        assertConfigAllPropertiesEquals(expected, actual);
    }
}
