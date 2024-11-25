package com.nazndev.securepublicapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_table")
@Getter
@Setter
public class OtpEntity {
    @Id
    private String mobileNumber;
    private String otp;
    private LocalDateTime expiryTime;
    private String nid;
    private String dob;


}
