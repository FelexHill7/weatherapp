package com.weather.weatherapp.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.weather.weatherapp.exception.WeatherAppException;
import com.weather.weatherapp.model.weatherRecord;
import com.weather.weatherapp.repository.weatherRepository;

@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final weatherRepository repository;
    private final ObjectMapper objectMapper;

    public ExportService(weatherRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public byte[] exportJson() {
        try {
            List<weatherRecord> records = repository.findAll();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(records);
        } catch (Exception e) {
            throw new WeatherAppException("Failed to export JSON: " + e.getMessage());
        }
    }

    public String exportCsv() {
        List<weatherRecord> records = repository.findAllByOrderByCreatedAtDesc();
        StringWriter sw = new StringWriter();
        String[] headers = {
                "ID", "Location", "Location Name", "Latitude", "Longitude",
                "Start Date", "End Date", "Avg Temp (°C)", "Max Temp (°C)", "Min Temp (°C)",
                "Description", "Weather Code", "Humidity (%)", "Wind Speed (km/h)", "Created At"
        };
        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT.builder().setHeader(headers).build())) {
            for (weatherRecord r : records) {
                printer.printRecord(
                        r.getId(),
                        r.getLocation(),
                        r.getLocationName(),
                        r.getLatitude(),
                        r.getLongitude(),
                        r.getStartDate() != null ? r.getStartDate().format(DATE_FMT) : "",
                        r.getEndDate() != null ? r.getEndDate().format(DATE_FMT) : "",
                        r.getTemperature(),
                        r.getTempMax(),
                        r.getTempMin(),
                        r.getDescription(),
                        r.getWeatherCode(),
                        r.getHumidity(),
                        r.getWindSpeed(),
                        r.getCreatedAt() != null ? r.getCreatedAt().format(DT_FMT) : "");
            }
        } catch (IOException e) {
            throw new WeatherAppException("Failed to export CSV: " + e.getMessage());
        }
        return sw.toString();
    }

    public String exportXml() {
        List<weatherRecord> records = repository.findAllByOrderByCreatedAtDesc();
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<weatherRecords>\n");
        for (weatherRecord r : records) {
            sb.append("  <record>\n");
            appendXml(sb, "id", String.valueOf(r.getId()));
            appendXml(sb, "location", r.getLocation());
            appendXml(sb, "locationName", r.getLocationName());
            appendXml(sb, "latitude", String.valueOf(r.getLatitude()));
            appendXml(sb, "longitude", String.valueOf(r.getLongitude()));
            appendXml(sb, "startDate", r.getStartDate() != null ? r.getStartDate().format(DATE_FMT) : "");
            appendXml(sb, "endDate", r.getEndDate() != null ? r.getEndDate().format(DATE_FMT) : "");
            appendXml(sb, "avgTemp", String.valueOf(r.getTemperature()));
            appendXml(sb, "maxTemp", String.valueOf(r.getTempMax()));
            appendXml(sb, "minTemp", String.valueOf(r.getTempMin()));
            appendXml(sb, "description", r.getDescription());
            appendXml(sb, "weatherCode", String.valueOf(r.getWeatherCode()));
            appendXml(sb, "humidity", String.valueOf(r.getHumidity()));
            appendXml(sb, "windSpeed", String.valueOf(r.getWindSpeed()));
            appendXml(sb, "createdAt", r.getCreatedAt() != null ? r.getCreatedAt().format(DT_FMT) : "");
            sb.append("  </record>\n");
        }
        sb.append("</weatherRecords>");
        return sb.toString();
    }

    public byte[] exportPdf() {
        List<weatherRecord> records = repository.findAllByOrderByCreatedAtDesc();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Weather Records Export")
                    .setFontSize(18).setBold());
            document.add(new Paragraph("Generated: " + java.time.LocalDateTime.now().format(DT_FMT))
                    .setFontSize(10));
            document.add(new Paragraph(" "));

            float[] colWidths = {30f, 80f, 60f, 55f, 55f, 55f, 80f, 60f};
            Table table = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();

            String[] cols = {"ID", "Location", "Start", "End", "Avg°C", "Max°C", "Description", "Created"};
            for (String col : cols) {
                table.addHeaderCell(new Cell().add(new Paragraph(col).setBold().setFontSize(8)));
            }

            for (weatherRecord r : records) {
                table.addCell(cell(String.valueOf(r.getId())));
                table.addCell(cell(truncate(r.getLocationName(), 20)));
                table.addCell(cell(r.getStartDate() != null ? r.getStartDate().format(DATE_FMT) : ""));
                table.addCell(cell(r.getEndDate() != null ? r.getEndDate().format(DATE_FMT) : ""));
                table.addCell(cell(r.getTemperature() + "°C"));
                table.addCell(cell(r.getTempMax() + "°C"));
                table.addCell(cell(truncate(r.getDescription(), 20)));
                table.addCell(cell(r.getCreatedAt() != null ? r.getCreatedAt().format(DT_FMT) : ""));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new WeatherAppException("Failed to export PDF: " + e.getMessage());
        }
        return baos.toByteArray();
    }

    public String exportMarkdown() {
        List<weatherRecord> records = repository.findAllByOrderByCreatedAtDesc();
        StringBuilder sb = new StringBuilder("# Weather Records Export\n\n");
        sb.append("**Generated:** ").append(java.time.LocalDateTime.now().format(DT_FMT)).append("\n\n");
        sb.append("| ID | Location | Start Date | End Date | Avg Temp | Max Temp | Min Temp | Description | Created |\n");
        sb.append("|----|----------|------------|----------|----------|----------|----------|-------------|--------|\n");
        for (weatherRecord r : records) {
            sb.append("| ").append(r.getId())
              .append(" | ").append(escape(r.getLocationName()))
              .append(" | ").append(r.getStartDate() != null ? r.getStartDate().format(DATE_FMT) : "")
              .append(" | ").append(r.getEndDate() != null ? r.getEndDate().format(DATE_FMT) : "")
              .append(" | ").append(r.getTemperature()).append("°C")
              .append(" | ").append(r.getTempMax()).append("°C")
              .append(" | ").append(r.getTempMin()).append("°C")
              .append(" | ").append(escape(r.getDescription()))
              .append(" | ").append(r.getCreatedAt() != null ? r.getCreatedAt().format(DT_FMT) : "")
              .append(" |\n");
        }
        return sb.toString();
    }

    private void appendXml(StringBuilder sb, String tag, String value) {
        sb.append("    <").append(tag).append(">")
          .append(value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;"))
          .append("</").append(tag).append(">\n");
    }

    private Cell cell(String text) {
        return new Cell().add(new Paragraph(text == null ? "" : text).setFontSize(7));
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("|", "\\|");
    }
}
