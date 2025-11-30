package com.recruitment.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ApplicationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Application getApplicationSample1() {
        return new Application().id(1L).uniqueNumber("uniqueNumber1");
    }

    public static Application getApplicationSample2() {
        return new Application().id(2L).uniqueNumber("uniqueNumber2");
    }

    public static Application getApplicationRandomSampleGenerator() {
        return new Application().id(longCount.incrementAndGet()).uniqueNumber(UUID.randomUUID().toString());
    }
}
