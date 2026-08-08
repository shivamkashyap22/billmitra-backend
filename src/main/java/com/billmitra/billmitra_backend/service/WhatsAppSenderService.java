package com.billmitra.billmitra_backend.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppSenderService {

    @Value("${twilio.whatsapp.number}")
    private String fromNumber;

    public void sendMessage(String to, String body) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromNumber),
                    body
            ).create();
            System.out.println("✅ Reply bheja: " + body);
        } catch (Exception e) {
            System.out.println("⚠️ WhatsApp send skip (trial limit): " + e.getMessage());
            System.out.println("📤 Reply hota: " + body);
        }
    }
}