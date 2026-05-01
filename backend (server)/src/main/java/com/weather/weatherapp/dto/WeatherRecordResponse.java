package com.weather.weatherapp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record WeatherRecordResponse(
        Long id,
        String location,
        String locationName,
        double latitude,
        double longitude,
        LocalDate startDate,
        LocalDate endDate,
        double temperature,
        double tempMax,
        double tempMin,
        String description,
        int weatherCode,
        String weatherEmoji,
        double humidity,
        double windSpeed,
        List<DailyWeatherData> dailyData,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
