package com.freightfox.weather.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freightfox.weather.entity.WeatherData;
import com.freightfox.weather.repository.WeatherRepository;

@Service
public class WeatherService {

	@Autowired
	private WeatherRepository weatherRepo;

	      //url's 
	 private static final String GOOGLE_MAPS_API_URL = "https://geocode.maps.co/search?q=%s&api_key=%s";
	  private static final String OPENWEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s";
 
	    @Value("${google.maps.api.key}")
	    private String googleMapsApiKey;

	    @Value("${openweather.api.key}")
	    private String openWeatherApiKey;
	
	public WeatherData getWeatherData(String pincode, LocalDate forDate) {
         //call for existing data in DB
		Optional<WeatherData> existingData = weatherRepo.findByPincodeAndDate(pincode, forDate);

		if(existingData.isPresent())
		{
		return existingData.get();
		}
        //calling method that will give latitude and longitude value from pincode
	String latLong=getLatLongFromPincode(pincode);
	String[] latlongarr=latLong.split(",");
	double latitute=Double.parseDouble(latlongarr[0]);
	double longitude=Double.parseDouble(latlongarr[1]);
          
	   //calling another method to get weather data
	String weatherInfo=getWeatherInfo(longitude,latitute);
	
	WeatherData data=new WeatherData();
	data.setPincode(pincode);
	data.setLatitute(latitute);
	data.setLongitute(longitude);
	data.setDate(forDate);
	data.setWeatherData(weatherInfo);
	return data;
   }
	//method to extract latitude and longitude data from pincode.
	public String getLatLongFromPincode(String pincode) {
		        
        // Call Google Maps API using restTemp
        String url = String.format(GOOGLE_MAPS_API_URL, pincode, googleMapsApiKey);
        System.out.println("Requesting URL: " + url); // Log the request URL
        
        //it is Sync call to RESTAPI
        RestTemplate restTemplate = new RestTemplate();
        
        String response = restTemplate.getForObject(url, String.class);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            
            JsonNode locationNode = jsonNode.path("results").get(0).path("geometry").path("location");
            String latitude = locationNode.path("lat").asText();
            String longitude = locationNode.path("lng").asText();
            
            return latitude + "," + longitude;
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }
    }
	
	// method to get weather data 
	public String getWeatherInfo(double latitude, double longitude) {
       
		String url = String.format(OPENWEATHER_API_URL, latitude, longitude, openWeatherApiKey);

		RestTemplate restTemplate = new RestTemplate();
        
		// Call Google Maps API using restTemp
        String response = restTemplate.getForObject(url, String.class);
        
        if (response == null || response.isEmpty()) {
            return "No data received from the weather service.";
        }

        JsonNode jsonNode = null;
		try {
			jsonNode = new ObjectMapper().readTree(response);
        if (jsonNode.path("cod").asInt() != 200) {
            return "Error: " + jsonNode.path("message").asText();
        }
		} catch (Exception e) {
			e.printStackTrace();
		}

        return String.format("Location: %s\nWeather: %s\nTemperature: %.2f K\nFeels Like: %.2f K\nHumidity: %d%%\nWind Speed: %.2f m/s",
            jsonNode.path("name").asText(),
            jsonNode.path("weather").get(0).path("description").asText(),
            jsonNode.path("main").path("temp").asDouble(),
            jsonNode.path("main").path("feels_like").asDouble(),
            jsonNode.path("main").path("humidity").asInt(),
            jsonNode.path("wind").path("speed").asDouble());
	}

}
