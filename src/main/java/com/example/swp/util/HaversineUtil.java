package com.example.swp.util;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component("haversineUtil")
public class HaversineUtil {
    @PostConstruct
    public void init() {
        System.out.println("HaversineUtil bean 'haversineUtil' has been initialized");
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in kilometers
    }
}