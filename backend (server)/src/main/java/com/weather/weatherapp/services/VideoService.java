package com.weather.weatherapp.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.weather.weatherapp.dto.VideoDto;

@Service
public class VideoService {

    private static final String YOUTUBE_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";

    @Value("${app.youtube-api-key:}")
    private String youtubeApiKey;

    private final RestClient restClient;

    public VideoService(RestClient restClient) {
        this.restClient = restClient;
    }

    @SuppressWarnings("unchecked")
    public List<VideoDto> searchVideos(String location) {
        if (youtubeApiKey == null || youtubeApiKey.isBlank()) {
            return List.of();
        }

        try {
            String query = location + " travel weather tourism";
            Map<String, Object> response = restClient.get()
                    .uri(YOUTUBE_SEARCH_URL
                                    + "?part=snippet&q={q}&maxResults=3&type=video&key={key}",
                            query, youtubeApiKey)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response == null) return List.of();

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            if (items == null) return List.of();

            return items.stream().map(item -> {
                Map<String, Object> id = (Map<String, Object>) item.get("id");
                String videoId = (String) id.get("videoId");

                Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                String title = (String) snippet.get("title");
                String description = (String) snippet.getOrDefault("description", "");
                String channelTitle = (String) snippet.getOrDefault("channelTitle", "");

                Map<String, Object> thumbnails = (Map<String, Object>) snippet.get("thumbnails");
                Map<String, Object> high = (Map<String, Object>) thumbnails.get("high");
                String thumbnailUrl = high != null ? (String) high.get("url") : "";

                return new VideoDto(
                        videoId, title, description,
                        thumbnailUrl,
                        "https://www.youtube.com/embed/" + videoId,
                        channelTitle);
            }).toList();

        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean isConfigured() {
        return youtubeApiKey != null && !youtubeApiKey.isBlank();
    }
}
