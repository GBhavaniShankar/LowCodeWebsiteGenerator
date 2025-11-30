package com.recruitment.backend.domain;

import static com.recruitment.backend.domain.ApplicantTestSamples.*;
import static com.recruitment.backend.domain.ApplicationFeeCategoryTestSamples.*;
import static com.recruitment.backend.domain.ApplicationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.recruitment.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApplicationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Application.class);
        Application application1 = getApplicationSample1();
        Application application2 = new Application();
        assertThat(application1).isNotEqualTo(application2);

        application2.setId(application1.getId());
        assertThat(application1).isEqualTo(application2);

        application2 = getApplicationSample2();
        assertThat(application1).isNotEqualTo(application2);
    }

    @Test
    void applicantTest() {
        Application application = getApplicationRandomSampleGenerator();
        Applicant applicantBack = getApplicantRandomSampleGenerator();

        application.setApplicant(applicantBack);
        assertThat(application.getApplicant()).isEqualTo(applicantBack);

        application.applicant(null);
        assertThat(application.getApplicant()).isNull();
    }

    @Test
    void feeCategoryTest() {
        Application application = getApplicationRandomSampleGenerator();
        ApplicationFeeCategory applicationFeeCategoryBack = getApplicationFeeCategoryRandomSampleGenerator();

        application.setFeeCategory(applicationFeeCategoryBack);
        assertThat(application.getFeeCategory()).isEqualTo(applicationFeeCategoryBack);

        application.feeCategory(null);
        assertThat(application.getFeeCategory()).isNull();
    }
}
