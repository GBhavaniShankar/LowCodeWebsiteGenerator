package com.recruitment.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ConfigTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Config getConfigSample1() {
        return new Config().id(1L).sampleFormUrl("sampleFormUrl1");
    }

    public static Config getConfigSample2() {
        return new Config().id(2L).sampleFormUrl("sampleFormUrl2");
    }

    public static Config getConfigRandomSampleGenerator() {
        return new Config().id(longCount.incrementAndGet()).sampleFormUrl(UUID.randomUUID().toString());
    }
}
