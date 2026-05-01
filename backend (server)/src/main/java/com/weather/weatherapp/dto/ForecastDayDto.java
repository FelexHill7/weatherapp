package com.weather.weatherapp.dto;

import java.time.LocalDate;

public record ForecastDayDto(
        LocalDate date,
        double tempMax,
        double tempMin,
        int weatherCode,
        String description,
        String weatherEmoji,
        int precipitationProbability,
        double windSpeedMax
) {}
