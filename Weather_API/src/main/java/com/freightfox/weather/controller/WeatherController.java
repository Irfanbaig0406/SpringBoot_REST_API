package com.freightfox.weather.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freightfox.weather.entity.WeatherData;
import com.freightfox.weather.service.WeatherService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

	    @Autowired
	    private WeatherService weatherService;

	    @GetMapping("/{pincode}/{for_date}")
	    public ResponseEntity<WeatherData> getWeather(@PathVariable String pincode, @PathVariable String for_date) {
	   
	    		 // Define the date format
	    	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	    	    LocalDate date;

	    	    
	    	    try {
	    	        date = LocalDate.parse(for_date, formatter);
	    	    } catch (DateTimeParseException e) {
	    	        return ResponseEntity.badRequest().body(null); 
	    	    }
	    	    // Call service class method 
	    	    WeatherData weather = weatherService.getWeatherData(pincode, date);
	    	    
	    	    if (weather == null) {
	    	        return ResponseEntity.notFound().build(); 
	    	    }

	    	    return ResponseEntity.ok(weather); 
	    	}
}
