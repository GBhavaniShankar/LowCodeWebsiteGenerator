package com.recruitment.backend.domain;

import static com.recruitment.backend.domain.ApplicantTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.recruitment.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ApplicantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Applicant.class);
        Applicant applicant1 = getApplicantSample1();
        Applicant applicant2 = new Applicant();
        assertThat(applicant1).isNotEqualTo(applicant2);

        applicant2.setId(applicant1.getId());
        assertThat(applicant1).isEqualTo(applicant2);

        applicant2 = getApplicantSample2();
        assertThat(applicant1).isNotEqualTo(applicant2);
    }
}
