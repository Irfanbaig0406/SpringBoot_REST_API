package com.freightfox.distance.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freightfox.distance.entity.RouteInfo;

@Repository
public interface RouteInfoRepository extends JpaRepository<RouteInfo, Long> {
    Optional<RouteInfo> findByFromPincodeAndToPincode(String from, String to);

}
