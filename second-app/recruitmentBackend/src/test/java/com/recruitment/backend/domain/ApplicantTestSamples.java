package com.recruitment.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ApplicantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Applicant getApplicantSample1() {
        return new Applicant()
            .id(1L)
            .username("username1")
            .email("email1")
            .passwordHash("passwordHash1")
            .firstName("firstName1")
            .lastName("lastName1")
            .authorities("authorities1");
    }

    public static Applicant getApplicantSample2() {
        return new Applicant()
            .id(2L)
            .username("username2")
            .email("email2")
            .passwordHash("passwordHash2")
            .firstName("firstName2")
            .lastName("lastName2")
            .authorities("authorities2");
    }

    public static Applicant getApplicantRandomSampleGenerator() {
        return new Applicant()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .passwordHash(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .authorities(UUID.randomUUID().toString());
    }
}
