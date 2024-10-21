package com.freightfox.weather.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;
	private String pincode;
	private Double longitute;
	private Double latitute;
    @Column(columnDefinition = "TEXT")
	private String weatherData;
    
    private LocalDate date;
    
    	
}
