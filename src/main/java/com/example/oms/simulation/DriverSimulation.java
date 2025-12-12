package com.example.oms.simulation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.oms.location.service.LocationService;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor
public class DriverSimulation {

    private final LocationService locationService;

    private double lat = 18.5204;
    private double lon = 73.8567;

    private double step = 0.0005; 

    private boolean movingUp = true;

    @Scheduled(fixedRate = 3000) 
    public void simulateMovement() {
        
        if (movingUp) {
            lat += step;
            lon += step;
            if (lat > 18.60) movingUp = false; // Turn around
        } else {
            lat -= step;
            lon -= step;
            if (lat < 18.50) movingUp = true; // Turn around
        }

        // --- Driver 1 (Alex) ---
        // Moves exactly on the line
        locationService.savePartnerLocation(1L, lat, lon);

        // --- Driver 2 (Bob) ---
        // Moves slightly offset (to the East)
        locationService.savePartnerLocation(4L, lat - 0.02, lon + 0.02);

        // --- Driver 3 (Charlie) ---
        // Moves slightly offset (to the West)
        locationService.savePartnerLocation(3L, lat + 0.01, lon - 0.01);

        System.out.println("🤖 Simulation: Updated locations for Partners 1, 2, 3 -> " + lat + ", " + lon);
    }
}