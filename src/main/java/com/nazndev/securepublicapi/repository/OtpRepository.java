package com.nazndev.securepublicapi.repository;

import com.nazndev.securepublicapi.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, String> {
    Optional<OtpEntity> findByMobileNumber(String mobileNumber);
}

