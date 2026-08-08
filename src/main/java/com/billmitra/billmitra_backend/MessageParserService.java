package com.billmitra.billmitra_backend.service;

import com.google.gson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.*;
import java.util.Map;

@Service
public class MessageParserService {

    @Value("${groq.api.key}")
    private String apiKey;

    public Map<String, String> parseMessage(String message) {
        try {
            String prompt = "Tum ek Indian dukaan billing assistant ho. " +
                    "Dukaandaar ka message parse karo aur sirf JSON do, kuch aur mat likho. " +
                    "Message: " + message + " " +
                    "JSON format: {\"intent\": \"UDHAAR_ADD ya PAYMENT_RECEIVED ya SUMMARY_TODAY ya UDHAAR_LIST ya UNKNOWN\", " +
                    "\"customer_name\": \"naam ya empty string\", \"amount\": \"amount ya empty string\", \"note\": \"note ya empty string\"} " +
                    "Example: Ramesh 500 udhaar = {\"intent\":\"UDHAAR_ADD\",\"customer_name\":\"Ramesh\",\"amount\":\"500\",\"note\":\"\"}";

            String requestBody = "{"
                    + "\"model\": \"llama-3.3-70b-versatile\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt.replace("\"", "\\\"") + "\"}],"
                    + "\"temperature\": 0.1"
                    + "}";

            System.out.println("🔄 Groq ko bhej raha hoon...");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("📨 Groq response: " + response.body());

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            String text = json.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            System.out.println("📝 Parsed text: " + text);

            // Clean karo
            text = text.replace("```json", "").replace("```", "").trim();
            int start = text.indexOf("{");
            int end = text.lastIndexOf("}");
            if (start != -1 && end != -1) {
                text = text.substring(start, end + 1);
            }

            JsonObject result = JsonParser.parseString(text).getAsJsonObject();

            return Map.of(
                    "intent", getStr(result, "intent"),
                    "customer_name", getStr(result, "customer_name"),
                    "amount", getStr(result, "amount"),
                    "note", getStr(result, "note")
            );

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return Map.of("intent", "UNKNOWN", "customer_name", "",
                    "amount", "", "note", "");
        }
    }

    private String getStr(JsonObject obj, String key) {
        try {
            JsonElement el = obj.get(key);
            if (el == null || el.isJsonNull()) return "";
            return el.getAsString();
        } catch (Exception e) {
            return "";
        }
    }
}