package com.weather.weatherapp.dto;

public record MapInfoDto(
        double latitude,
        double longitude,
        String locationName,
        String embedUrl,
        String viewUrl
) {}
