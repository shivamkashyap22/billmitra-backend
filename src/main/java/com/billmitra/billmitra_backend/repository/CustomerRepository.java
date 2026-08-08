package com.billmitra.billmitra_backend.repository;

import com.billmitra.billmitra_backend.model.Customer;
import com.billmitra.billmitra_backend.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNameAndShop(String name, Shop shop);
    List<Customer> findByShopAndTotalUdhaarGreaterThan(Shop shop, Double amount);
}