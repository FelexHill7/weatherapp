package com.weather.weatherapp.dto;

public record CurrentWeatherDto(
        String locationName,
        String country,
        double latitude,
        double longitude,
        double temperature,
        double feelsLike,
        double humidity,
        double windSpeed,
        int weatherCode,
        String description,
        String weatherEmoji,
        String localTime
) {}
