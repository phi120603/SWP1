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

    private final String MAPBOX_TOKEN = "pk.eyJ1IjoicXVhbjI4IiwiYSI6ImNtZDd1eGMzMTBubjQycnBzNmF4ZjVtcTgifQ.sNclRIXKR1W9H1RTaPbaDg";

    public Optional<double[]> geocode(String address) {
        if (address == null || address.trim().isEmpty()) {
            System.out.println("Geocoding skipped: Address is null or empty");
            return Optional.empty();
        }

        try {
            String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + encoded +
                    ".json?access_token=" + MAPBOX_TOKEN;

            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(res.body());

            // Log full JSON response for debugging
            System.out.println("Mapbox geocode result for address: " + address);
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));

            if (json.has("features") && json.get("features").size() > 0) {
                // Prioritize results with place_type = address or place
                for (JsonNode feature : json.get("features")) {
                    JsonNode placeType = feature.get("place_type");
                    if (placeType != null && placeType.isArray()) {
                        for (JsonNode type : placeType) {
                            String typeStr = type.asText();
                            if ("address".equals(typeStr) || "place".equals(typeStr)) {
                                JsonNode center = feature.get("center");
                                double lon = center.get(0).asDouble();
                                double lat = center.get(1).asDouble();
                                System.out.println("Selected feature with place_type: " + typeStr + ", Coordinates: [" + lat + ", " + lon + "]");
                                return Optional.of(new double[]{lat, lon});
                            }
                        }
                    }
                }

                // Fallback to the first feature if no address/place is found
                JsonNode center = json.get("features").get(0).get("center");
                double lon = center.get(0).asDouble();
                double lat = center.get(1).asDouble();
                System.out.println("Fallback to first feature, Coordinates: [" + lat + ", " + lon + "]");
                return Optional.of(new double[]{lat, lon});
            } else {
                System.out.println("No features found in Mapbox response for address: " + address);
            }

        } catch (Exception e) {
            System.err.println("Geocoding error for address: " + address);
            e.printStackTrace();
        }
        return Optional.empty();
    }
}