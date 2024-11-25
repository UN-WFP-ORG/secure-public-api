package com.nazndev.securepublicapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CaptchaService {

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.verify-url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verifyCaptcha(String recaptchaResponse) {
        Map<String, String> body = new HashMap<>();
        body.put("secret", secretKey);
        body.put("response", recaptchaResponse);

        Map<String, Object> response = restTemplate.postForObject(
                verifyUrl + "?secret={secret}&response={response}",
                null,
                Map.class,
                body
        );

        return response != null && (Boolean) response.get("success");
    }
}
