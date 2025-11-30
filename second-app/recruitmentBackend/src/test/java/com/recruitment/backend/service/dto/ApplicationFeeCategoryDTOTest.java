package com.recruitment.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.recruitment.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApplicationFeeCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApplicationFeeCategoryDTO.class);
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO1 = new ApplicationFeeCategoryDTO();
        applicationFeeCategoryDTO1.setId(1L);
        ApplicationFeeCategoryDTO applicationFeeCategoryDTO2 = new ApplicationFeeCategoryDTO();
        assertThat(applicationFeeCategoryDTO1).isNotEqualTo(applicationFeeCategoryDTO2);
        applicationFeeCategoryDTO2.setId(applicationFeeCategoryDTO1.getId());
        assertThat(applicationFeeCategoryDTO1).isEqualTo(applicationFeeCategoryDTO2);
        applicationFeeCategoryDTO2.setId(2L);
        assertThat(applicationFeeCategoryDTO1).isNotEqualTo(applicationFeeCategoryDTO2);
        applicationFeeCategoryDTO1.setId(null);
        assertThat(applicationFeeCategoryDTO1).isNotEqualTo(applicationFeeCategoryDTO2);
    }
}
