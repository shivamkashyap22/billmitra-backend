package com.billmitra.billmitra_backend.webhook;

import com.billmitra.billmitra_backend.service.MessageParserService;
import com.billmitra.billmitra_backend.service.UdhaarService;
import com.billmitra.billmitra_backend.service.WhatsAppSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {

    @Autowired
    private MessageParserService parserService;

    @Autowired
    private UdhaarService udhaarService;

    @Autowired
    private WhatsAppSenderService senderService;

    @PostMapping("/whatsapp")
    public ResponseEntity<String> receiveMessage(
            @RequestParam Map<String, String> params) {

        String from = params.get("From");
        String body = params.get("Body");

        System.out.println("📱 From: " + from);
        System.out.println("💬 Message: " + body);

        Map<String, String> parsed = parserService.parseMessage(body);
        String intent = parsed.get("intent");
        String reply = "";

        switch (intent) {
            case "UDHAAR_ADD":
                reply = udhaarService.processUdhaar(from, parsed);
                break;
            case "PAYMENT_RECEIVED":
                reply = udhaarService.processPayment(from, parsed);
                break;
            case "UDHAAR_LIST":
                reply = udhaarService.getUdhaarList(from);
                break;
            case "SUMMARY_TODAY":
                reply = udhaarService.getTodaySummary(from);
                break;
            default:
                reply = "🤔 Samajh nahi aaya. Try karo:\n" +
                        "'Ramesh 500 udhaar'\n" +
                        "'Ramesh ne 500 diya'\n" +
                        "'sabka udhaar'\n" +
                        "'aaj ki sale'";
        }


        // Reply bhejo
        senderService.sendMessage(from, reply);

        return ResponseEntity.ok("<Response></Response>");
    }
}