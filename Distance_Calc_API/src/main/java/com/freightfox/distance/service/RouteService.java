package com.freightfox.distance.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.freightfox.distance.Repository.PincodeRepository;
import com.freightfox.distance.Repository.RouteInfoRepository;
import com.freightfox.distance.entity.Pincode;
import com.freightfox.distance.entity.RouteInfo;

@Service
public class RouteService {
    @Autowired
    private RouteInfoRepository routeInfoRepository;

    @Autowired
    private PincodeRepository pincodeRepository;

    @Value("${traveltime.api.key}") 
    private String travelTimeApiKey;

    @Value("${google.maps.api.key}") 
    private String goggleApiKey;
    
    // Method to get distance and duration
    public RouteInfo getRouteInfo(String fromPincode, String toPincode) {
        // Check if the route info is in the DB
        Optional<RouteInfo> routeInfoOptional = routeInfoRepository.findByFromPincodeAndToPincode(fromPincode, toPincode);
        if (routeInfoOptional.isPresent()) {
            return routeInfoOptional.get();
        } else {
            validatePincodes(fromPincode, toPincode);
            // Call API to get route info if not found in DB
            return fetchFromTravelTimeAPI(fromPincode, toPincode);
        }
    }

    private void validatePincodes(String fromPincode, String toPincode) {
        if (!pincodeRepository.findByPincode(fromPincode).isPresent()) {
            fetchPincodeFromAPI(fromPincode);
        }
        if (!pincodeRepository.findByPincode(toPincode).isPresent()) {
            fetchPincodeFromAPI(toPincode);
        }
    }

    private RouteInfo fetchFromTravelTimeAPI(String fromPincode, String toPincode) {
        RestTemplate restTemplate = new RestTemplate();

        // Retrieve latitude and longitude for the pincodes
        Pincode fromLocation = pincodeRepository.findByPincode(fromPincode)
                .orElseGet(() -> fetchPincodeFromAPI(fromPincode));
        Pincode toLocation = pincodeRepository.findByPincode(toPincode)
                .orElseGet(() -> fetchPincodeFromAPI(toPincode));

        String url = String.format("https://api.traveltime.com/v4/routes?key=%s&origin=%s,%s&destination=%s,%s",
                travelTimeApiKey, fromLocation.getLatitude(), fromLocation.getLongitude(),
                toLocation.getLatitude(), toLocation.getLongitude());

        // Call API
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("routes")) {
            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
            if (!routes.isEmpty()) {
                Map<String, Object> route = routes.get(0);
                double distance = (double) route.get("distance"); 
                long duration = ((Number) route.get("duration")).longValue();             

                RouteInfo routeInfo = new RouteInfo();
                routeInfo.setFromPincode(fromPincode);
                routeInfo.setToPincode(toPincode);
                routeInfo.setDistance(distance);
                routeInfo.setDuration(duration);
                routeInfo.setRouteDetails(route.toString()); 

                // Save to database
                routeInfoRepository.save(routeInfo);
                return routeInfo;
            }
        }
        throw new RuntimeException("No routes found for the provided pin codes.");
    }

    private Pincode fetchPincodeFromAPI(String pincode) {
    	
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("https://geocode.maps.co/search?q=%s&api_key=%s", pincode,goggleApiKey);

        // Call API
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("pincode")) {
            Map<String, Object> pincodeData = (Map<String, Object>) response.get("pincode");
            Pincode pincodeEntity = new Pincode();
            pincodeEntity.setPincode(pincode);
            pincodeEntity.setLatitude((double) pincodeData.get("latitude"));
            pincodeEntity.setLongitude((double) pincodeData.get("longitude"));

            // Save to database
            pincodeRepository.save(pincodeEntity);
            return pincodeEntity;
        }
        throw new RuntimeException("No pincode found for the provided pincode.");
    }
}