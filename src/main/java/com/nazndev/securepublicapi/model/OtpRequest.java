package com.nazndev.securepublicapi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequest {
    private String mobileNumber;
    private String nid;
    private String dob;
    private String captchaToken;

}

