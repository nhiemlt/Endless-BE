package com.datn.endless.repositories;

import java.util.List;
import java.util.Optional;

import com.datn.endless.models.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {

    Optional<PaymentStatus> findByStatusName(String statusName);
}
