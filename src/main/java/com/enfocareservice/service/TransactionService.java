package com.enfocareservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enfocareservice.entity.Transaction;
import com.enfocareservice.repository.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	// ✅ Retrieve all transactions
	public List<Transaction> getAllTransactions() {
		List<Transaction> transactions = transactionRepository.findAll();
		System.out.println("Fetched Transactions: " + transactions.size());
		return transactions;
	}

	// ✅ Save a transaction (Missing method - ADD THIS!)
	public Transaction saveTransaction(Transaction transaction) {
		return transactionRepository.save(transaction); // ✅ Saves transaction to the database
	}

	// ✅ Log a transaction (Used in webhook)
	public void logTransaction(String orderId, String email, Double amount, String currency, Instant paymentDate,
			String paymentMethod, String subscriptionType, String receiptUrl) {
		Transaction transaction = new Transaction();
		transaction.setOrderId(orderId);
		transaction.setEmail(email);
		transaction.setAmount(BigDecimal.valueOf(amount)); // ✅ Convert Double → BigDecimal
		transaction.setCurrency(currency);
		transaction.setPaymentDate(paymentDate);
		transaction.setPaymentMethod(paymentMethod);
		transaction.setSubscriptionType(subscriptionType);
		transaction.setReceiptUrl(receiptUrl);

		transactionRepository.save(transaction); // ✅ Save to database
	}
}
