package com.weather.weatherapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "weather_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class weatherRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    @Column(length = 500)
    private String locationName;

    private double latitude;
    private double longitude;

    private LocalDate startDate;
    private LocalDate endDate;

    private double temperature;
    private double tempMax;
    private double tempMin;

    @Column(length = 500)
    private String description;

    private int weatherCode;
    private double humidity;
    private double windSpeed;

    @Column(columnDefinition = "TEXT")
    private String dailyDataJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
