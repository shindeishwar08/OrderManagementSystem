package com.example.oms.location.service;

import java.util.List;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String GEO_KEY = "oms:partner:locations";

    public void savePartnerLocation(Long partnerId, double lat, double lon) {
        
        Point point = new Point(lon, lat);
        
        redisTemplate.opsForGeo().add(GEO_KEY, point, partnerId.toString());
    }

    public Point getPartnerLocation(Long partnerId){

        List<Point> positions = redisTemplate.opsForGeo().position(GEO_KEY, partnerId.toString());

        if(positions==null || positions.isEmpty()) return null;

        return positions.get(0);
    }
}