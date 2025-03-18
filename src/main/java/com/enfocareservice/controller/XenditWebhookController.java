package com.enfocareservice.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enfocareservice.service.TransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/webhook/xendit")
public class XenditWebhookController {

	@Value("${xendit.webhook.token}") // ✅ Load token from properties file
	private String xenditSecretToken;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping("/invoice-paid")
	public ResponseEntity<String> handleInvoicePaid(@RequestBody String payload,
			@RequestHeader(value = "x-callback-token", required = false) String token) {

		// 🔍 Debugging log (Optional: remove in production)
		System.out.println("Received webhook request with token: " + token);

		// ✅ Step 1: Verify Webhook Token
		if (token == null || !token.equals(xenditSecretToken)) {
			System.out.println("Invalid or missing Xendit token!");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
		}

		try {
			// ✅ Step 2: Parse JSON payload
			JsonNode jsonNode = objectMapper.readTree(payload);

			if (!"PAID".equalsIgnoreCase(jsonNode.get("status").asText())) {
				return ResponseEntity.badRequest().body("Transaction is not paid");
			}

			String orderId = jsonNode.get("id").asText();
			String email = jsonNode.get("payer_email").asText();
			String subscriptionType = jsonNode.get("description").asText();
			double amount = jsonNode.get("amount").asDouble();
			Instant paymentDate = Instant.parse(jsonNode.get("paid_at").asText());
			String receiptUrl = jsonNode.get("invoice_url").asText();
			String paymentMethod = jsonNode.get("payment_method").asText(); // Payment method

			// ✅ Handle missing fields gracefully
			String currency = jsonNode.has("currency") ? jsonNode.get("currency").asText() : "IDR";

			// ✅ Step 3: Log transaction using TransactionService
			transactionService.logTransaction(orderId, email, amount, currency, paymentDate, paymentMethod,
					subscriptionType, receiptUrl);

			return ResponseEntity.ok("Webhook processed successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing webhook: " + e.getMessage());
		}
	}
}
