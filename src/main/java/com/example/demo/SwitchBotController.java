package com.example.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwitchBotController {

  @Value("d56c702099336df7eaeee2f2ca72681245b59d3ac685b05ddab1d8a6cfc9d54587eb20fabb7ef063ed6a50d4cdb9daaf")
  private String token;

  @Value("cffecbdd7874fcd6dc385caaa37dc718")
  private String secret;

  @GetMapping("/switchbot")
  public String getDevices() {
    try {
      String nonce = UUID.randomUUID().toString();
      String time = String.valueOf(Instant.now().toEpochMilli());
      String data = token + time + nonce;

      SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(secretKeySpec);
      String signature = Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));

      HttpRequest getDevices = HttpRequest.newBuilder()
          .uri(new URI("https://api.switch-bot.com/v1.1/devices"))
          .header("Authorization", token)
          .header("sign", signature)
          .header("nonce", nonce)
          .header("t", time)
          .GET()
          .build();

      HttpClient client = HttpClient.newBuilder().build();
      HttpResponse<String> response = client.send(getDevices, BodyHandlers.ofString());

      return response.body();
    } catch (Exception e) {
      e.printStackTrace();
      return "Error: " + e.getMessage();
    }
  }
}
