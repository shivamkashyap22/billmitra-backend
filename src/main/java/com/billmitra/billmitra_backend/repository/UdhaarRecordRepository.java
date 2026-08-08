package com.billmitra.billmitra_backend.repository;

import com.billmitra.billmitra_backend.model.Shop;
import com.billmitra.billmitra_backend.model.UdhaarRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface UdhaarRecordRepository extends JpaRepository<UdhaarRecord, Long> {
    List<UdhaarRecord> findByShopAndCreatedAtAfter(Shop shop, LocalDateTime dateTime);
}