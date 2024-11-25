package com.nazndev.securepublicapi.service;

import com.nazndev.securepublicapi.config.JwtConfig;
import com.nazndev.securepublicapi.entity.OtpEntity;
import com.nazndev.securepublicapi.model.OtpRequest;
import com.nazndev.securepublicapi.model.OtpValidationRequest;
import com.nazndev.securepublicapi.repository.OtpRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;

    private final CaptchaService captchaService;

    public ResponseEntity<String> sendOtp(OtpRequest request) {
        // Validate inputs
        if (!isValidInput(request)) {
            System.out.println("Invalid Input: " + request);
            return ResponseEntity.badRequest().body("Invalid Input");
        }

        // Validate CAPTCHA
        if (!captchaService.verifyCaptcha(request.getCaptchaToken())) {
            System.out.println("Invalid CAPTCHA for request: " + request);
            return ResponseEntity.status(403).body("Invalid CAPTCHA");
        }

        // Generate and store OTP
        String otp = generateOtp();
        saveOtpToDatabase(request.getMobileNumber(), otp, request.getNid(), request.getDob());

        // Simulate SMS sending
        boolean isSmsSent = sendSms(request.getMobileNumber(), otp);
        if (isSmsSent) {
            System.out.println("OTP sent successfully to: " + request.getMobileNumber());
            return ResponseEntity.ok("OTP Sent");
        } else {
            System.err.println("Failed to send OTP to: " + request.getMobileNumber());
            return ResponseEntity.status(500).body("Failed to send OTP");
        }
    }



    public ResponseEntity<String> validateOtp(OtpValidationRequest request) {
        System.out.println("Received Validation Request: " + request);

        // Validate input fields
        if (request.getMobileNumber() == null || request.getOtp() == null ||
                request.getNid() == null || request.getDob() == null) {
            return ResponseEntity.badRequest().body("Invalid input: All fields are required.");
        }

        // Retrieve OTP
        Optional<OtpEntity> otpEntityOpt = otpRepository.findByMobileNumber(request.getMobileNumber());
        if (otpEntityOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No OTP found for this mobile number.");
        }

        OtpEntity otpEntity = otpEntityOpt.get();

        // Log the retrieved and provided data for debugging
        System.out.println("Stored DOB: '" + otpEntity.getDob() + "'");
        System.out.println("Request DOB: '" + request.getDob() + "'");

        // Normalize and compare DOB
        String requestDob = request.getDob().trim();
        String storedDob = otpEntity.getDob().trim();
        if (!requestDob.equals(storedDob)) {
            System.out.println("Invalid DOB: Expected '" + storedDob + "', Got '" + requestDob + "'");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Date of Birth.");
        }

        // Check OTP
        if (!otpEntity.getOtp().equals(request.getOtp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP. Please try again.");
        }

        // Check expiry
        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP has expired. Please request a new one.");
        }

        // Check NID
        if (!otpEntity.getNid().equals(request.getNid())) {
            System.out.println("Invalid NID: Expected " + otpEntity.getNid() + ", Got " + request.getNid());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid NID.");
        }

        // Generate JWT and return
        String jwtToken = generateAccessToken(request.getMobileNumber(), request.getNid(), request.getDob());
        return ResponseEntity.ok(jwtToken);
    }



    public ResponseEntity<String> fetchData(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(JwtConfig.SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid Token");
        }

        String nid = claims.get("nid", String.class);
        String dob = claims.get("dob", String.class);
        return ResponseEntity.ok("Data for NID: " + nid + ", DOB: " + dob);
    }

    private boolean isValidInput(OtpRequest request) {
        return request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()
                && request.getNid() != null && !request.getNid().isEmpty()
                && request.getDob() != null && !request.getDob().isEmpty();
    }


    private String generateOtp() {
        return String.valueOf(new Random().nextInt(899999) + 100000); // 6-digit OTP
    }

    private void saveOtpToDatabase(String mobileNumber, String otp, String nid, String dob) {
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setMobileNumber(mobileNumber);
        otpEntity.setOtp(otp);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpEntity.setNid(nid);
        otpEntity.setDob(dob);

        otpRepository.save(otpEntity);

        System.out.println("Saved OTP: " + otp + " for mobile number: " + mobileNumber);
    }


    private boolean sendSms(String mobileNumber, String otp) {
        System.out.println("Sent OTP: " + otp + " to Mobile: " + mobileNumber);
        return true; // Simulated SMS service
    }

    private String generateAccessToken(String mobileNumber, String nid, String dob) {
        return Jwts.builder()
                .setSubject(mobileNumber)
                .claim("nid", nid)
                .claim("dob", dob)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000)) // 5 minutes validity
                .signWith(SignatureAlgorithm.HS256, JwtConfig.SECRET_KEY)
                .compact();
    }
}
