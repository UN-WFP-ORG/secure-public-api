package com.nazndev.securepublicapi.controller;

import com.nazndev.securepublicapi.model.OtpRequest;
import com.nazndev.securepublicapi.model.OtpValidationRequest;
import com.nazndev.securepublicapi.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpRestController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest request) {
        return otpService.sendOtp(request);
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateOtp(@RequestBody OtpValidationRequest request) {
        return otpService.validateOtp(request);
    }

    @PostMapping("/data")
    public ResponseEntity<String> fetchData(@RequestHeader("Authorization") String token) {
        return otpService.fetchData(token);
    }

}

