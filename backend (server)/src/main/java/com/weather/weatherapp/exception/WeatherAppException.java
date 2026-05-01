package com.weather.weatherapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WeatherAppException extends RuntimeException {
    public WeatherAppException(String message) {
        super(message);
    }

    public WeatherAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
