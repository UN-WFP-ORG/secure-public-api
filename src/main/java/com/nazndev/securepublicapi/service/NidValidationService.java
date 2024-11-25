package com.nazndev.securepublicapi.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class NidValidationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> validateNid(String nid, String dob) {
        String url = "http://159.89.210.92/ext/api/validate-nid";

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("vwb", "vwb");

        // Create payload
        Map<String, String> payload = new HashMap<>();
        payload.put("nid", nid);
        payload.put("dob", dob);

        // Create request entity
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);

        // Send POST request
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // Return the response body as a map
        return response.getBody();
    }
}
