package com.weather.weatherapp.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.weatherapp.dto.DailyWeatherData;
import com.weather.weatherapp.dto.LocationInfo;
import com.weather.weatherapp.dto.WeatherQueryRequest;
import com.weather.weatherapp.dto.WeatherRecordResponse;
import com.weather.weatherapp.exception.ResourceNotFoundException;
import com.weather.weatherapp.exception.WeatherAppException;
import com.weather.weatherapp.model.weatherRecord;
import com.weather.weatherapp.repository.weatherRepository;

@Service
@Transactional
public class weatherService {

    private final weatherRepository repository;
    private final GeocodingService geocodingService;
    private final OpenMeteoService openMeteoService;
    private final ObjectMapper objectMapper;

    public weatherService(weatherRepository repository,
                          GeocodingService geocodingService,
                          OpenMeteoService openMeteoService,
                          ObjectMapper objectMapper) {
        this.repository = repository;
        this.geocodingService = geocodingService;
        this.openMeteoService = openMeteoService;
        this.objectMapper = objectMapper;
    }

    public WeatherRecordResponse createRecord(WeatherQueryRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new WeatherAppException("End date must be on or after start date");
        }
        if (request.startDate().plusYears(1).isBefore(request.endDate())) {
            throw new WeatherAppException("Date range cannot exceed one year");
        }

        LocationInfo location = geocodingService.geocode(request.location());
        List<DailyWeatherData> dailyData = openMeteoService.getWeatherForDateRange(
                location.latitude(), location.longitude(), request.startDate(), request.endDate());

        if (dailyData.isEmpty()) {
            throw new WeatherAppException("No weather data available for the specified date range");
        }

        double avgTemp = dailyData.stream()
                .mapToDouble(DailyWeatherData::tempAvg)
                .average().orElse(0);
        double maxTemp = dailyData.stream()
                .mapToDouble(DailyWeatherData::tempMax)
                .max().orElse(0);
        double minTemp = dailyData.stream()
                .mapToDouble(DailyWeatherData::tempMin)
                .min().orElse(0);
        int dominantCode = dailyData.stream()
                .mapToInt(DailyWeatherData::weatherCode)
                .findFirst().orElse(0);

        String dailyJson = serializeDailyData(dailyData);

        weatherRecord record = weatherRecord.builder()
                .location(request.location())
                .locationName(location.displayName())
                .latitude(location.latitude())
                .longitude(location.longitude())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .temperature(Math.round(avgTemp * 10.0) / 10.0)
                .tempMax(Math.round(maxTemp * 10.0) / 10.0)
                .tempMin(Math.round(minTemp * 10.0) / 10.0)
                .description(OpenMeteoService.describeCode(dominantCode))
                .weatherCode(dominantCode)
                .humidity(0)
                .windSpeed(0)
                .dailyDataJson(dailyJson)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        weatherRecord saved = repository.save(record);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<WeatherRecordResponse> getAllRecords() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WeatherRecordResponse getRecord(Long id) {
        weatherRecord record = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Weather record not found with id: " + id));
        return toResponse(record);
    }

    public WeatherRecordResponse updateRecord(Long id, WeatherQueryRequest request) {
        weatherRecord existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Weather record not found with id: " + id));

        if (request.endDate().isBefore(request.startDate())) {
            throw new WeatherAppException("End date must be on or after start date");
        }
        if (request.startDate().plusYears(1).isBefore(request.endDate())) {
            throw new WeatherAppException("Date range cannot exceed one year");
        }

        LocationInfo location = geocodingService.geocode(request.location());
        List<DailyWeatherData> dailyData = openMeteoService.getWeatherForDateRange(
                location.latitude(), location.longitude(), request.startDate(), request.endDate());

        if (dailyData.isEmpty()) {
            throw new WeatherAppException("No weather data available for the specified date range");
        }

        double avgTemp = dailyData.stream().mapToDouble(DailyWeatherData::tempAvg).average().orElse(0);
        double maxTemp = dailyData.stream().mapToDouble(DailyWeatherData::tempMax).max().orElse(0);
        double minTemp = dailyData.stream().mapToDouble(DailyWeatherData::tempMin).min().orElse(0);
        int dominantCode = dailyData.stream().mapToInt(DailyWeatherData::weatherCode).findFirst().orElse(0);

        existing.setLocation(request.location());
        existing.setLocationName(location.displayName());
        existing.setLatitude(location.latitude());
        existing.setLongitude(location.longitude());
        existing.setStartDate(request.startDate());
        existing.setEndDate(request.endDate());
        existing.setTemperature(Math.round(avgTemp * 10.0) / 10.0);
        existing.setTempMax(Math.round(maxTemp * 10.0) / 10.0);
        existing.setTempMin(Math.round(minTemp * 10.0) / 10.0);
        existing.setDescription(OpenMeteoService.describeCode(dominantCode));
        existing.setWeatherCode(dominantCode);
        existing.setDailyDataJson(serializeDailyData(dailyData));
        existing.setUpdatedAt(LocalDateTime.now());

        return toResponse(repository.save(existing));
    }

    public void deleteRecord(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Weather record not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private WeatherRecordResponse toResponse(weatherRecord r) {
        List<DailyWeatherData> daily = deserializeDailyData(r.getDailyDataJson());
        return new WeatherRecordResponse(
                r.getId(), r.getLocation(), r.getLocationName(),
                r.getLatitude(), r.getLongitude(),
                r.getStartDate(), r.getEndDate(),
                r.getTemperature(), r.getTempMax(), r.getTempMin(),
                r.getDescription(), r.getWeatherCode(),
                OpenMeteoService.emojiForCode(r.getWeatherCode()),
                r.getHumidity(), r.getWindSpeed(),
                daily, r.getCreatedAt(), r.getUpdatedAt());
    }

    private String serializeDailyData(List<DailyWeatherData> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<DailyWeatherData> deserializeDailyData(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<DailyWeatherData>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
