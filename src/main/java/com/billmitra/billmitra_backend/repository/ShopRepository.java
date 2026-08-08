package com.billmitra.billmitra_backend.repository;

import com.billmitra.billmitra_backend.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByPhoneNumber(String phoneNumber);
}