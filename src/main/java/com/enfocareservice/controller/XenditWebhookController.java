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

			if (!"PAID".equalsIgnoreCase(jsonNode.path("status").asText())) {
				return ResponseEntity.badRequest().body("Transaction is not paid");
			}

			// Use `.path("fieldName")` instead of `.get("fieldName")` to avoid
			// NullPointerException
			String orderId = jsonNode.path("id").asText(null);
			String email = jsonNode.path("payer_email").asText(null);
			String subscriptionType = jsonNode.path("description").asText(null);
			double amount = jsonNode.path("amount").asDouble(0); // Default 0 if missing
			Instant paymentDate = jsonNode.has("paid_at") ? Instant.parse(jsonNode.get("paid_at").asText())
					: Instant.now();
			String receiptUrl = jsonNode.has("invoice_url") ? jsonNode.get("invoice_url").asText() : null;
			String paymentMethod = jsonNode.path("payment_method").asText(null);

			// ✅ Handle missing fields gracefully
			String currency = jsonNode.path("currency").asText("IDR");

			// ✅ Step 3: Log transaction
			transactionService.logTransaction(orderId, email, amount, currency, paymentDate, paymentMethod,
					subscriptionType, receiptUrl);

			return ResponseEntity.ok("Webhook processed successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing webhook: " + e.getMessage());
		}

	}
}
