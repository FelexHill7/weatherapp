package com.weather.weatherapp.dto;

public record LocationInfo(
        String name,
        String country,
        String admin1,
        double latitude,
        double longitude,
        String displayName
) {}
