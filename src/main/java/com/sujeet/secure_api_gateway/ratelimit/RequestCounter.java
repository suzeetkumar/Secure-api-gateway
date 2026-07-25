package com.sujeet.secure_api_gateway.ratelimit;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestCounter {

    private final AtomicInteger count;
    private volatile long windowStartMillis;

    public RequestCounter(long windowStartMillis) {
        this.count = new AtomicInteger(1);
        this.windowStartMillis = windowStartMillis;
    }

    public int incrementAndGet() {
        return count.incrementAndGet();
    }

    public long getWindowStartMillis() {
        return windowStartMillis;
    }

    public void resetWindow(long newWindowStartMillis) {
        count.set(1);
        windowStartMillis = newWindowStartMillis;
    }
}
