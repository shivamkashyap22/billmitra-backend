package com.billmitra.billmitra_backend.service;

import com.billmitra.billmitra_backend.model.*;
import com.billmitra.billmitra_backend.repository.CustomerRepository;
import com.billmitra.billmitra_backend.repository.ShopRepository;
import com.billmitra.billmitra_backend.repository.UdhaarRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UdhaarService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UdhaarRecordRepository udhaarRecordRepository;

    @Autowired
    private ShopRepository shopRepository;

    public String processUdhaar(String fromNumber, Map<String, String> parsed) {
        try {
            String customerName = parsed.get("customer_name");
            String amountStr = parsed.get("amount");

            if (customerName.isEmpty() || amountStr.isEmpty()) {
                return "❌ Naam ya amount samajh nahi aaya. Dobara likho jaise: Ramesh 500 udhaar";
            }

            Double amount = Double.parseDouble(amountStr);

            Shop shop = shopRepository.findByPhoneNumber(fromNumber)
                    .orElseGet(() -> {
                        Shop s = new Shop();
                        s.setPhoneNumber(fromNumber);
                        s.setName("My Shop");
                        s.setOwnerName("Owner");
                        return shopRepository.save(s);
                    });

            Customer customer = customerRepository
                    .findByNameAndShop(customerName, shop)
                    .orElseGet(() -> {
                        Customer c = new Customer();
                        c.setName(customerName);
                        c.setShop(shop);
                        c.setTotalUdhaar(0.0);
                        return customerRepository.save(c);
                    });

            UdhaarRecord record = new UdhaarRecord();
            record.setShop(shop);
            record.setCustomer(customer);
            record.setAmount(amount);
            record.setNote(parsed.get("note"));
            udhaarRecordRepository.save(record);

            customer.setTotalUdhaar(customer.getTotalUdhaar() + amount);
            customerRepository.save(customer);

            return "✅ Record ho gaya!\n\n" +
                    "👤 " + customerName + "\n" +
                    "💰 ₹" + amount.intValue() + " udhaar\n" +
                    "📊 Total pending: ₹" + customer.getTotalUdhaar().intValue();

        } catch (Exception e) {
            System.out.println("❌ Udhaar error: " + e.getMessage());
            return "❌ Kuch error hua, dobara try karo";
        }
    }

    public String processPayment(String fromNumber, Map<String, String> parsed) {
        try {
            String customerName = parsed.get("customer_name");
            String amountStr = parsed.get("amount");

            if (customerName.isEmpty() || amountStr.isEmpty()) {
                return "❌ Naam ya amount samajh nahi aaya.";
            }

            Double amount = Double.parseDouble(amountStr);

            Shop shop = shopRepository.findByPhoneNumber(fromNumber)
                    .orElse(null);
            if (shop == null) return "❌ Pehle koi udhaar record karo.";

            Customer customer = customerRepository
                    .findByNameAndShop(customerName, shop)
                    .orElse(null);
            if (customer == null) {
                return "❌ " + customerName + " ka koi record nahi mila.";
            }

            double newTotal = customer.getTotalUdhaar() - amount;
            if (newTotal < 0) newTotal = 0;
            customer.setTotalUdhaar(newTotal);
            customerRepository.save(customer);

            UdhaarRecord record = new UdhaarRecord();
            record.setShop(shop);
            record.setCustomer(customer);
            record.setAmount(-amount);
            record.setStatus("PAID");
            record.setNote("Payment received");
            udhaarRecordRepository.save(record);

            return "✅ Payment record ho gaya!\n\n" +
                    "👤 " + customerName + "\n" +
                    "💰 ₹" + amount.intValue() + " mila\n" +
                    "📊 Abhi bhi pending: ₹" + (int) newTotal;

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    public String getUdhaarList(String fromNumber) {
        try {
            Shop shop = shopRepository.findByPhoneNumber(fromNumber)
                    .orElse(null);
            if (shop == null) return "❌ Koi record nahi mila.";

            List<Customer> customers = customerRepository
                    .findByShopAndTotalUdhaarGreaterThan(shop, 0.0);

            if (customers.isEmpty()) {
                return "🎉 Sabka udhaar clear hai!";
            }

            StringBuilder sb = new StringBuilder("📋 Pending udhaar list:\n\n");
            double total = 0;
            for (Customer c : customers) {
                sb.append("👤 ").append(c.getName())
                        .append(" — ₹").append(c.getTotalUdhaar().intValue())
                        .append("\n");
                total += c.getTotalUdhaar();
            }
            sb.append("\n💰 Total: ₹").append((int) total);
            return sb.toString();

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    public String getTodaySummary(String fromNumber) {
        try {
            Shop shop = shopRepository.findByPhoneNumber(fromNumber)
                    .orElse(null);
            if (shop == null) return "❌ Koi record nahi mila.";

            List<UdhaarRecord> records = udhaarRecordRepository
                    .findByShopAndCreatedAtAfter(shop,
                            LocalDateTime.now().withHour(0).withMinute(0).withSecond(0));

            double totalUdhaar = 0;
            double totalPayment = 0;

            for (UdhaarRecord r : records) {
                if (r.getAmount() > 0) totalUdhaar += r.getAmount();
                else totalPayment += Math.abs(r.getAmount());
            }

            return "📊 Aaj ka summary:\n\n" +
                    "📝 Udhaar diya: ₹" + (int) totalUdhaar + "\n" +
                    "💵 Payment mila: ₹" + (int) totalPayment + "\n" +
                    "📈 Net: ₹" + (int) (totalPayment - totalUdhaar);

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}