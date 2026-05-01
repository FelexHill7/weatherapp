package com.weather.weatherapp.dto;

public record VideoDto(
        String videoId,
        String title,
        String description,
        String thumbnailUrl,
        String embedUrl,
        String channelTitle
) {}
