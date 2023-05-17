package com.aummn.eventbridge.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class OrderNumberGenerator {
    private final String prefix;
    private final AtomicLong counter = new AtomicLong(1);

    public OrderNumberGenerator() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();
        this.prefix = formatter.format(now);
    }

    public String generateOrderNumber() {
        long currentCounterValue = counter.getAndIncrement();
        return prefix + String.format("%06d", currentCounterValue);
    }
}
