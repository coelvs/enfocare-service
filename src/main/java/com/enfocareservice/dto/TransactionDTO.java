package com.enfocareservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
	private String orderId; // Unique transaction/order ID
	private String email; // Patient's email
	private BigDecimal amount; // Use BigDecimal to prevent rounding issues
	private String currency; // Currency (e.g., PHP, USD, IDR)
	private Instant paymentDate; // Timestamp of payment
	private String paymentMethod; // Payment method (e.g., Credit Card, Bank Transfer)
	private String subscriptionType; // Subscription details (Monthly, Annual, etc.)
	private String receiptUrl; // Link to e-receipt
}
