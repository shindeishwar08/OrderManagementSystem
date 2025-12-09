package com.example.oms.location.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String GEO_KEY = "oms:partner:locations";

    public void savePartnerLocation(Long partnerId, double lat, double lon) {
        // Redis expects (Longitude, Latitude)
        Point point = new Point(lon, lat);
        redisTemplate.opsForGeo().add(GEO_KEY, point, partnerId.toString());
    }

    public Point getPartnerLocation(Long partnerId) {
        List<Point> positions = redisTemplate.opsForGeo().position(GEO_KEY, partnerId.toString());

        if (positions == null || positions.isEmpty()) {
            return null;
        }
        return positions.get(0);
    }

    public List<String> findNearestPartners(double lat, double lon, double radiusKm) {
        
        Circle searchArea = new Circle(new Point(lon, lat), new Distance(radiusKm, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().search(GEO_KEY, searchArea);

        if(results==null) return new ArrayList<>();

        List<String> partnerIds = new ArrayList<>();

        for(GeoResult<RedisGeoCommands.GeoLocation<String>> result: results){
            partnerIds.add(result.getContent().getName());
        }

        return partnerIds;

    }
}