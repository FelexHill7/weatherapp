package com.weather.weatherapp.dto;

import java.time.LocalDate;

public record DailyWeatherData(
        LocalDate date,
        double tempMax,
        double tempMin,
        double tempAvg,
        int weatherCode,
        String description,
        double precipitationSum,
        double windSpeedMax
) {}
