package com.freightfox.weather.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freightfox.weather.entity.WeatherData;
@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {

	Optional<WeatherData> findByPincodeAndDate(String pincode, LocalDate forDate);
	
	
}
