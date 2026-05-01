package com.weather.weatherapp.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.weather.weatherapp.dto.CurrentWeatherDto;
import com.weather.weatherapp.dto.DailyWeatherData;
import com.weather.weatherapp.dto.ForecastDayDto;
import com.weather.weatherapp.dto.ForecastResponse;
import com.weather.weatherapp.dto.LocationInfo;
import com.weather.weatherapp.exception.WeatherAppException;

@Service
public class OpenMeteoService {

    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String ARCHIVE_URL = "https://archive-api.open-meteo.com/v1/archive";

    private final RestClient restClient;

    public OpenMeteoService(RestClient restClient) {
        this.restClient = restClient;
    }

    public CurrentWeatherDto getCurrentWeather(LocationInfo location) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(FORECAST_URL
                            + "?latitude={lat}&longitude={lon}"
                            + "&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m"
                            + "&timezone=auto",
                            location.latitude(), location.longitude())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response == null) throw new WeatherAppException("No weather data available");

            @SuppressWarnings("unchecked")
            Map<String, Object> current = (Map<String, Object>) response.get("current");

            double temp = ((Number) current.get("temperature_2m")).doubleValue();
            double feelsLike = ((Number) current.get("apparent_temperature")).doubleValue();
            double humidity = ((Number) current.get("relative_humidity_2m")).doubleValue();
            double windSpeed = ((Number) current.get("wind_speed_10m")).doubleValue();
            int weatherCode = ((Number) current.get("weather_code")).intValue();
            String localTime = (String) current.get("time");

            return new CurrentWeatherDto(
                    location.displayName(),
                    location.country(),
                    location.latitude(),
                    location.longitude(),
                    temp, feelsLike, humidity, windSpeed,
                    weatherCode,
                    describeCode(weatherCode),
                    emojiForCode(weatherCode),
                    localTime);
        } catch (WeatherAppException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherAppException("Failed to fetch current weather: " + e.getMessage());
        }
    }

    public ForecastResponse getForecast(LocationInfo location) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(FORECAST_URL
                            + "?latitude={lat}&longitude={lon}"
                            + "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,wind_speed_10m_max"
                            + "&forecast_days=5&timezone=auto",
                            location.latitude(), location.longitude())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            List<ForecastDayDto> days = parseForecastDays(response);
            return new ForecastResponse(location.displayName(), location.latitude(), location.longitude(), days);
        } catch (WeatherAppException e) {
            throw e;
        } catch (Exception e) {
            throw new WeatherAppException("Failed to fetch forecast: " + e.getMessage());
        }
    }

    public List<DailyWeatherData> getWeatherForDateRange(double lat, double lon, LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        LocalDate archiveCutoff = today.minusDays(92);

        if (!start.isBefore(archiveCutoff)) {
            return fetchFromForecastApi(lat, lon, start, end);
        } else if (end.isBefore(archiveCutoff)) {
            return fetchFromArchiveApi(lat, lon, start, end);
        } else {
            List<DailyWeatherData> combined = new ArrayList<>();
            combined.addAll(fetchFromArchiveApi(lat, lon, start, archiveCutoff.minusDays(1)));
            combined.addAll(fetchFromForecastApi(lat, lon, archiveCutoff, end));
            return combined;
        }
    }

    @SuppressWarnings("unchecked")
    private List<DailyWeatherData> fetchFromForecastApi(double lat, double lon, LocalDate start, LocalDate end) {
        Map<String, Object> response = restClient.get()
                .uri(FORECAST_URL
                        + "?latitude={lat}&longitude={lon}"
                        + "&start_date={start}&end_date={end}"
                        + "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,wind_speed_10m_max"
                        + "&timezone=auto",
                        lat, lon, start.toString(), end.toString())
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        return parseDailyData(response, true);
    }

    @SuppressWarnings("unchecked")
    private List<DailyWeatherData> fetchFromArchiveApi(double lat, double lon, LocalDate start, LocalDate end) {
        Map<String, Object> response = restClient.get()
                .uri(ARCHIVE_URL
                        + "?latitude={lat}&longitude={lon}"
                        + "&start_date={start}&end_date={end}"
                        + "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max"
                        + "&timezone=auto",
                        lat, lon, start.toString(), end.toString())
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        return parseDailyData(response, false);
    }

    @SuppressWarnings("unchecked")
    private List<DailyWeatherData> parseDailyData(Map<String, Object> response, boolean hasWeatherCode) {
        if (response == null) return List.of();

        Map<String, Object> daily = (Map<String, Object>) response.get("daily");
        if (daily == null) return List.of();

        List<String> times = (List<String>) daily.get("time");
        List<Number> maxTemps = (List<Number>) daily.get("temperature_2m_max");
        List<Number> minTemps = (List<Number>) daily.get("temperature_2m_min");
        List<Number> precip = (List<Number>) daily.getOrDefault("precipitation_sum",
                daily.getOrDefault("precipitation_probability_max", List.of()));
        List<Number> wind = (List<Number>) daily.getOrDefault("wind_speed_10m_max", List.of());
        List<Number> codes = hasWeatherCode ? (List<Number>) daily.get("weather_code") : null;

        List<DailyWeatherData> result = new ArrayList<>();
        if (times == null) return result;

        for (int i = 0; i < times.size(); i++) {
            double tMax = maxTemps != null && i < maxTemps.size() && maxTemps.get(i) != null
                    ? maxTemps.get(i).doubleValue() : 0;
            double tMin = minTemps != null && i < minTemps.size() && minTemps.get(i) != null
                    ? minTemps.get(i).doubleValue() : 0;
            double precipVal = precip != null && i < precip.size() && precip.get(i) != null
                    ? precip.get(i).doubleValue() : 0;
            double windVal = wind != null && i < wind.size() && wind.get(i) != null
                    ? wind.get(i).doubleValue() : 0;
            int code = codes != null && i < codes.size() && codes.get(i) != null
                    ? codes.get(i).intValue() : 0;

            result.add(new DailyWeatherData(
                    LocalDate.parse(times.get(i)),
                    tMax, tMin,
                    Math.round((tMax + tMin) / 2.0 * 10.0) / 10.0,
                    code,
                    describeCode(code),
                    precipVal, windVal));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<ForecastDayDto> parseForecastDays(Map<String, Object> response) {
        if (response == null) return List.of();
        Map<String, Object> daily = (Map<String, Object>) response.get("daily");
        if (daily == null) return List.of();

        List<String> times = (List<String>) daily.get("time");
        List<Number> codes = (List<Number>) daily.get("weather_code");
        List<Number> maxTemps = (List<Number>) daily.get("temperature_2m_max");
        List<Number> minTemps = (List<Number>) daily.get("temperature_2m_min");
        List<Number> precip = (List<Number>) daily.getOrDefault("precipitation_probability_max", List.of());
        List<Number> wind = (List<Number>) daily.getOrDefault("wind_speed_10m_max", List.of());

        List<ForecastDayDto> days = new ArrayList<>();
        if (times == null) return days;

        for (int i = 0; i < times.size(); i++) {
            int code = codes != null && i < codes.size() && codes.get(i) != null ? codes.get(i).intValue() : 0;
            double tMax = maxTemps != null && i < maxTemps.size() && maxTemps.get(i) != null
                    ? maxTemps.get(i).doubleValue() : 0;
            double tMin = minTemps != null && i < minTemps.size() && minTemps.get(i) != null
                    ? minTemps.get(i).doubleValue() : 0;
            int precipPct = precip != null && i < precip.size() && precip.get(i) != null
                    ? precip.get(i).intValue() : 0;
            double windVal = wind != null && i < wind.size() && wind.get(i) != null
                    ? wind.get(i).doubleValue() : 0;

            days.add(new ForecastDayDto(
                    LocalDate.parse(times.get(i)),
                    tMax, tMin, code,
                    describeCode(code),
                    emojiForCode(code),
                    precipPct, windVal));
        }
        return days;
    }

    public static String describeCode(int code) {
        return switch (code) {
            case 0 -> "Clear Sky";
            case 1 -> "Mainly Clear";
            case 2 -> "Partly Cloudy";
            case 3 -> "Overcast";
            case 45, 48 -> "Foggy";
            case 51, 53, 55 -> "Drizzle";
            case 61 -> "Light Rain";
            case 63 -> "Moderate Rain";
            case 65 -> "Heavy Rain";
            case 71 -> "Light Snow";
            case 73 -> "Moderate Snow";
            case 75 -> "Heavy Snow";
            case 77 -> "Snow Grains";
            case 80, 81 -> "Rain Showers";
            case 82 -> "Violent Showers";
            case 85, 86 -> "Snow Showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with Hail";
            default -> "Unknown";
        };
    }

    public static String emojiForCode(int code) {
        if (code == 0) return "☀️";
        if (code == 1) return "🌤️";
        if (code == 2) return "⛅";
        if (code == 3) return "☁️";
        if (code <= 48) return "🌫️";
        if (code <= 67) return "🌧️";
        if (code <= 77) return "❄️";
        if (code <= 82) return "🌦️";
        if (code <= 86) return "🌨️";
        if (code <= 99) return "⛈️";
        return "🌡️";
    }
}
