package com.recruitment.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AdvertisementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Advertisement getAdvertisementSample1() {
        return new Advertisement().id(1L).title("title1");
    }

    public static Advertisement getAdvertisementSample2() {
        return new Advertisement().id(2L).title("title2");
    }

    public static Advertisement getAdvertisementRandomSampleGenerator() {
        return new Advertisement().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}
