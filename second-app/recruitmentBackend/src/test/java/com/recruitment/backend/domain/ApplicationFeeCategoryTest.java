package com.recruitment.backend.domain;

import static com.recruitment.backend.domain.ApplicationFeeCategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.recruitment.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApplicationFeeCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ApplicationFeeCategory.class);
        ApplicationFeeCategory applicationFeeCategory1 = getApplicationFeeCategorySample1();
        ApplicationFeeCategory applicationFeeCategory2 = new ApplicationFeeCategory();
        assertThat(applicationFeeCategory1).isNotEqualTo(applicationFeeCategory2);

        applicationFeeCategory2.setId(applicationFeeCategory1.getId());
        assertThat(applicationFeeCategory1).isEqualTo(applicationFeeCategory2);

        applicationFeeCategory2 = getApplicationFeeCategorySample2();
        assertThat(applicationFeeCategory1).isNotEqualTo(applicationFeeCategory2);
    }
}
