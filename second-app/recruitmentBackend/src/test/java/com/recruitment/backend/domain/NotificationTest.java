package com.recruitment.backend.domain;

import static com.recruitment.backend.domain.ApplicantTestSamples.*;
import static com.recruitment.backend.domain.NotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.recruitment.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void recipientTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Applicant applicantBack = getApplicantRandomSampleGenerator();

        notification.setRecipient(applicantBack);
        assertThat(notification.getRecipient()).isEqualTo(applicantBack);

        notification.recipient(null);
        assertThat(notification.getRecipient()).isNull();
    }
}
