package com.weather.weatherapp.services;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.weather.weatherapp.dto.LocationInfo;
import com.weather.weatherapp.exception.WeatherAppException;

@Service
public class GeocodingService {

    private static final String OPEN_METEO_GEO = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String NOMINATIM = "https://nominatim.openstreetmap.org";

    private final RestClient restClient;

    public GeocodingService(RestClient restClient) {
        this.restClient = restClient;
    }

    public LocationInfo geocode(String location) {
        if (isCoordinates(location)) {
            return fromCoordinates(location);
        }
        try {
            return geocodeWithOpenMeteo(location);
        } catch (WeatherAppException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherAppException("Failed to find location: " + location);
        }
    }

    private boolean isCoordinates(String location) {
        return location.matches("-?\\d+(\\.\\d+)?,\\s*-?\\d+(\\.\\d+)?");
    }

    @SuppressWarnings("unchecked")
    private LocationInfo fromCoordinates(String location) {
        String[] parts = location.split(",");
        double lat = Double.parseDouble(parts[0].trim());
        double lon = Double.parseDouble(parts[1].trim());

        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new WeatherAppException("Coordinates out of valid range");
        }

        try {
            List<Map<String, Object>> results = restClient.get()
                    .uri(NOMINATIM + "/reverse?lat={lat}&lon={lon}&format=json", lat, lon)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            if (results != null && !results.isEmpty()) {
                Map<String, Object> r = results.get(0);
                Map<String, Object> address = (Map<String, Object>) r.getOrDefault("address", Map.of());
                String name = (String) address.getOrDefault("city",
                        address.getOrDefault("town", address.getOrDefault("village", "Unknown")));
                String country = (String) address.getOrDefault("country", "");
                String admin1 = (String) address.getOrDefault("state", "");
                String displayName = name + (admin1.isEmpty() ? "" : ", " + admin1) + ", " + country;
                return new LocationInfo(name, country, admin1, lat, lon, displayName);
            }
        } catch (Exception ignored) {
        }

        return new LocationInfo(
                String.format("%.4f, %.4f", lat, lon),
                "",
                "",
                lat,
                lon,
                String.format("%.4f°N, %.4f°E", lat, lon));
    }

    @SuppressWarnings("unchecked")
    private LocationInfo geocodeWithOpenMeteo(String location) {
        Map<String, Object> response = restClient.get()
                .uri(OPEN_METEO_GEO + "?name={name}&count=5&language=en&format=json", location)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (response == null) {
            throw new WeatherAppException("Location not found: " + location);
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        if (results == null || results.isEmpty()) {
            throw new WeatherAppException(
                    "Location not found: '" + location + "'. Please check the spelling and try again.");
        }

        Map<String, Object> first = results.get(0);
        String name = (String) first.get("name");
        String country = (String) first.getOrDefault("country", "");
        String admin1 = (String) first.getOrDefault("admin1", "");
        double lat = ((Number) first.get("latitude")).doubleValue();
        double lon = ((Number) first.get("longitude")).doubleValue();
        String displayName = name
                + (admin1 != null && !admin1.isEmpty() ? ", " + admin1 : "")
                + (country != null && !country.isEmpty() ? ", " + country : "");

        return new LocationInfo(name, country, admin1 != null ? admin1 : "", lat, lon, displayName);
    }
}
