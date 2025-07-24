package com.example.swp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class GeocodingService {

    private final String MAPBOX_TOKEN = "pk.eyJ1IjoicXVhbjI4IiwiYSI6ImNtZDd1eGMzMTBubjQycnBzNmF4ZjVtcTgifQ.sNclRIXKR1W9H1RTaPbaDg"; // ← thay bằng token của bạn

    public Optional<double[]> geocode(String address) {
        try {
            String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + encoded +
                    ".json?access_token=" + MAPBOX_TOKEN;

            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());

            JsonNode json = new ObjectMapper().readTree(res.body());
            if (json.has("features") && json.get("features").size() > 0) {
                JsonNode center = json.get("features").get(0).get("center");
                return Optional.of(new double[]{center.get(1).asDouble(), center.get(0).asDouble()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
