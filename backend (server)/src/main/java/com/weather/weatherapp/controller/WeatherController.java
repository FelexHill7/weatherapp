package com.weather.weatherapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weather.weatherapp.dto.CurrentWeatherDto;
import com.weather.weatherapp.dto.ForecastResponse;
import com.weather.weatherapp.dto.LocationInfo;
import com.weather.weatherapp.dto.MapInfoDto;
import com.weather.weatherapp.dto.VideoDto;
import com.weather.weatherapp.dto.WeatherQueryRequest;
import com.weather.weatherapp.dto.WeatherRecordResponse;
import com.weather.weatherapp.services.GeocodingService;
import com.weather.weatherapp.services.OpenMeteoService;
import com.weather.weatherapp.services.VideoService;
import com.weather.weatherapp.services.weatherService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/weather")
@Validated
public class WeatherController {

    private final weatherService service;
    private final GeocodingService geocodingService;
    private final OpenMeteoService openMeteoService;
    private final VideoService videoService;

    public WeatherController(weatherService service,
                             GeocodingService geocodingService,
                             OpenMeteoService openMeteoService,
                             VideoService videoService) {
        this.service = service;
        this.geocodingService = geocodingService;
        this.openMeteoService = openMeteoService;
        this.videoService = videoService;
    }

    // ── Current weather (not stored) ──────────────────────────────────────────

    @GetMapping("/current")
    public ResponseEntity<CurrentWeatherDto> getCurrentWeather(@RequestParam String location) {
        LocationInfo loc = geocodingService.geocode(location);
        CurrentWeatherDto weather = openMeteoService.getCurrentWeather(loc);
        return ResponseEntity.ok(weather);
    }

    // ── 5-day forecast (not stored) ───────────────────────────────────────────

    @GetMapping("/forecast")
    public ResponseEntity<ForecastResponse> getForecast(@RequestParam String location) {
        LocationInfo loc = geocodingService.geocode(location);
        ForecastResponse forecast = openMeteoService.getForecast(loc);
        return ResponseEntity.ok(forecast);
    }

    // ── CRUD: Create ──────────────────────────────────────────────────────────

    @PostMapping("/records")
    public ResponseEntity<WeatherRecordResponse> createRecord(
            @Valid @RequestBody WeatherQueryRequest request) {
        WeatherRecordResponse created = service.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── CRUD: Read (all) ──────────────────────────────────────────────────────

    @GetMapping("/records")
    public ResponseEntity<List<WeatherRecordResponse>> getAllRecords() {
        return ResponseEntity.ok(service.getAllRecords());
    }

    // ── CRUD: Read (one) ──────────────────────────────────────────────────────

    @GetMapping("/records/{id}")
    public ResponseEntity<WeatherRecordResponse> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRecord(id));
    }

    // ── CRUD: Update ──────────────────────────────────────────────────────────

    @PutMapping("/records/{id}")
    public ResponseEntity<WeatherRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody WeatherQueryRequest request) {
        return ResponseEntity.ok(service.updateRecord(id, request));
    }

    // ── CRUD: Delete ──────────────────────────────────────────────────────────

    @DeleteMapping("/records/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        service.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    // ── YouTube videos ────────────────────────────────────────────────────────

    @GetMapping("/videos")
    public ResponseEntity<Map<String, Object>> getVideos(@RequestParam String location) {
        List<VideoDto> videos = videoService.searchVideos(location);
        return ResponseEntity.ok(Map.of(
                "videos", videos,
                "youtubeConfigured", videoService.isConfigured()));
    }

    // ── Map info ──────────────────────────────────────────────────────────────

    @GetMapping("/map")
    public ResponseEntity<MapInfoDto> getMapInfo(@RequestParam String location) {
        LocationInfo loc = geocodingService.geocode(location);
        double lat = loc.latitude();
        double lon = loc.longitude();
        double delta = 0.05;

        String embedUrl = String.format(
                "https://www.openstreetmap.org/export/embed.html?bbox=%.6f,%.6f,%.6f,%.6f&layer=mapnik&marker=%.6f,%.6f",
                lon - delta, lat - delta, lon + delta, lat + delta, lat, lon);

        String viewUrl = String.format(
                "https://www.openstreetmap.org/?mlat=%.6f&mlon=%.6f#map=12/%.6f/%.6f",
                lat, lon, lat, lon);

        return ResponseEntity.ok(new MapInfoDto(lat, lon, loc.displayName(), embedUrl, viewUrl));
    }
}
