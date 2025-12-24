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
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String GEO_KEY = "oms:partner:locations";
    private static final long STALE_THRESHOLD_MS = 600000;

    public void savePartnerLocation(Long partnerId, double lat, double lon) {
        String memberId = String.valueOf(partnerId);
        
        String dataKey = "oms:partner:data:" + partnerId; 

        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) {
                // Command 1: Geo Index
                operations.opsForGeo().add(GEO_KEY, new Point(lon, lat), memberId);

                // Command 2: Metadata Sidecar
                operations.opsForHash().put(dataKey, "lastUpdated", String.valueOf(System.currentTimeMillis()));
                operations.opsForHash().put(dataKey, "isAvailable", "true");

                return null;
            }
        });
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

        return filterStalePartners(partnerIds);

    }

    public List<String> filterStalePartners(List<String> partnerIds){
        if(partnerIds.isEmpty()) return partnerIds;

        List<Object> timestamps = redisTemplate.executePipelined(new SessionCallback<Object>() {

            // Pipeline: Fetch "lastUpdated" for ALL partners in one go
            public Object execute(RedisOperations operations){
                for(String id:partnerIds){
                    operations.opsForHash().get("oms:partner:data:"+id, "lastUpdated");
                }
                return null;
            }
        });

        // Java Side: Filter out the old ones
        Long currTime = System.currentTimeMillis();
        List<String> validPartners = new ArrayList<>();


        for(int i=0;i<partnerIds.size();i++){
            Object time = timestamps.get(i);

            if(time !=null){
                try {
                    long updatedTime = Long.parseLong((String)time);
                    if(currTime-updatedTime<STALE_THRESHOLD_MS) validPartners.add(partnerIds.get(i));
                } catch (NumberFormatException e) {
                }
            }
        }
        return validPartners;
    }

    
}