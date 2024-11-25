package com.nazndev.securepublicapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpValidationRequest {
    private String mobileNumber;
    private String otp;
    private String nid;
    private String dob;
}

