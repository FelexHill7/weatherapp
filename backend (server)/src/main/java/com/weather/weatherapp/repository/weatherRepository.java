package com.weather.weatherapp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.weather.weatherapp.model.weatherRecord;

public interface weatherRepository extends JpaRepository<weatherRecord, Long> {

    List<weatherRecord> findByLocationContainingIgnoreCase(String location);

    List<weatherRecord> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(
            LocalDate startDate, LocalDate endDate);

    @Query("SELECT w FROM weatherRecord w WHERE LOWER(w.locationName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<weatherRecord> findByLocationNameContaining(@Param("name") String name);

    List<weatherRecord> findAllByOrderByCreatedAtDesc();
}
