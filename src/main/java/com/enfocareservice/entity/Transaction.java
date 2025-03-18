package com.enfocareservice.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderId; // Xendit Order ID
	private String email; // Patient's email
	private BigDecimal amount; // Use BigDecimal for monetary values
	private String currency; // Currency used (PHP, IDR, USD, etc.)
	private Instant paymentDate; // Payment timestamp
	private String paymentMethod; // Credit Card, Bank Transfer, etc.
	private String subscriptionType; // Monthly, Annual, etc.
	private String receiptUrl; // Xendit receipt URL
}
