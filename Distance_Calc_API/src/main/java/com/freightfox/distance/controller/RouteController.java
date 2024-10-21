package com.freightfox.distance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freightfox.distance.entity.RouteInfo;
import com.freightfox.distance.service.RouteService;

@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired
    private RouteService routeService;

    @GetMapping("/distance")
    public ResponseEntity<RouteInfo> getDistanceAndDuration(
            @RequestParam String fromPincode,
            @RequestParam String toPincode) {
        RouteInfo routeInfo = routeService.getRouteInfo(fromPincode, toPincode);
        return ResponseEntity.ok(routeInfo);
    }
}