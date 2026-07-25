package com.sujeet.secure_api_gateway.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter extends jakarta.servlet.http.HttpFilter {

    private final ConcurrentHashMap<String, RequestCounter> clientCounters = new ConcurrentHashMap<>();

    @Value("${rate-limit.max-requests}")
    private int maxRequests;

    @Value("${rate-limit.window-seconds}")
    private int windowSeconds;

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws ServletException, IOException {

        String clientKey = resolveClientKey(request);
        long windowMillis = TimeUnit.SECONDS.toMillis(windowSeconds);
        long now = System.currentTimeMillis();

        RequestCounter counter = clientCounters.computeIfAbsent(clientKey,
                key -> new RequestCounter(now));

        int currentCount;

        synchronized (counter) {
            if (now - counter.getWindowStartMillis() > windowMillis) {
                counter.resetWindow(now);
                currentCount = 1;
            } else {
                currentCount = counter.incrementAndGet();
            }
        }

        if (currentCount > maxRequests) {
            response.setStatus(429);
            response.setContentType("text/plain");
            response.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return "user:" + authHeader;
        }

        return "ip:" + request.getRemoteAddr();
    }
}
