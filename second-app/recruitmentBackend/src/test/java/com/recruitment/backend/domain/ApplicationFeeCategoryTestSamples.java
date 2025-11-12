package com.recruitment.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ApplicationFeeCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ApplicationFeeCategory getApplicationFeeCategorySample1() {
        return new ApplicationFeeCategory().id(1L).name("name1");
    }

    public static ApplicationFeeCategory getApplicationFeeCategorySample2() {
        return new ApplicationFeeCategory().id(2L).name("name2");
    }

    public static ApplicationFeeCategory getApplicationFeeCategoryRandomSampleGenerator() {
        return new ApplicationFeeCategory().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
