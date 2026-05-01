package com.weather.weatherapp.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.weather.weatherapp.exception.WeatherAppException;
import com.weather.weatherapp.services.ExportService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping
    public ResponseEntity<?> export(@RequestParam(defaultValue = "json") String format) {
        String date = LocalDate.now().toString();

        return switch (format.toLowerCase()) {
            case "json" -> {
                byte[] data = exportService.exportJson();
                yield ResponseEntity.ok()
                        .headers(downloadHeaders("weather-records-" + date + ".json", MediaType.APPLICATION_JSON))
                        .body(data);
            }
            case "csv" -> {
                byte[] data = exportService.exportCsv().getBytes(StandardCharsets.UTF_8);
                yield ResponseEntity.ok()
                        .headers(downloadHeaders("weather-records-" + date + ".csv",
                                MediaType.parseMediaType("text/csv")))
                        .body(data);
            }
            case "xml" -> {
                byte[] data = exportService.exportXml().getBytes(StandardCharsets.UTF_8);
                yield ResponseEntity.ok()
                        .headers(downloadHeaders("weather-records-" + date + ".xml",
                                MediaType.APPLICATION_XML))
                        .body(data);
            }
            case "pdf" -> {
                byte[] data = exportService.exportPdf();
                yield ResponseEntity.ok()
                        .headers(downloadHeaders("weather-records-" + date + ".pdf",
                                MediaType.APPLICATION_PDF))
                        .body(data);
            }
            case "markdown", "md" -> {
                byte[] data = exportService.exportMarkdown().getBytes(StandardCharsets.UTF_8);
                yield ResponseEntity.ok()
                        .headers(downloadHeaders("weather-records-" + date + ".md",
                                MediaType.parseMediaType("text/markdown")))
                        .body(data);
            }
            default -> throw new WeatherAppException(
                    "Unsupported format: " + format + ". Supported: json, csv, xml, pdf, markdown");
        };
    }

    private HttpHeaders downloadHeaders(String filename, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename).build());
        return headers;
    }
}
