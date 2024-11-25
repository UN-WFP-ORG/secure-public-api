package com.nazndev.securepublicapi.controller;

import com.nazndev.securepublicapi.model.OtpRequest;
import com.nazndev.securepublicapi.model.OtpValidationRequest;
import com.nazndev.securepublicapi.service.NidValidationService;
import com.nazndev.securepublicapi.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class OtpViewController {

    private final OtpService otpService;
    private final NidValidationService nidValidationService;

    @Value("${recaptcha.site-key}")
    private String recaptchaSiteKey;

    @GetMapping("/")
    public String redirectToOtpForm() {
        return "redirect:/otp/form";
    }

    @GetMapping("/otp/form")
    public String showOtpForm(Model model) {
        model.addAttribute("siteKey", recaptchaSiteKey);
        return "otpForm";
    }


    @PostMapping("/otp/sendOtp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest request) {
        System.out.println("Received Payload: " + request);

        if (!isValidInput(request)) {
            System.out.println("Invalid Input: " + request);
            return ResponseEntity.badRequest().body("Invalid Input");
        }

        // Simulate OTP sending
        String response = otpService.sendOtp(request).getBody();
        return ResponseEntity.ok(response);
    }

    private boolean isValidInput(OtpRequest request) {
        return request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()
                && request.getNid() != null && !request.getNid().isEmpty()
                && request.getDob() != null && !request.getDob().isEmpty()
                && request.getCaptchaToken() != null && !request.getCaptchaToken().isEmpty();
    }



    @PostMapping("/otp/validateOtp")
    @ResponseBody
    public ResponseEntity<?> validateOtp(@RequestBody OtpValidationRequest request) {
        try {
            // Validate input fields
            if (request.getMobileNumber() == null || request.getOtp() == null ||
                    request.getNid() == null || request.getDob() == null) {
                return ResponseEntity.badRequest().body("All fields are required (mobileNumber, otp, nid, dob).");
            }

            // Directly use the service response
            return otpService.validateOtp(request);

        } catch (Exception e) {
            // Log and return server error
            System.err.println("Exception during OTP validation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

}
