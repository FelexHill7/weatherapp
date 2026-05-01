package com.weather.weatherapp.dto;

import java.util.List;

public record ForecastResponse(
        String locationName,
        double latitude,
        double longitude,
        List<ForecastDayDto> days
) {}
