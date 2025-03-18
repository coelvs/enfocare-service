package com.enfocareservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.enfocareservice.dto.TransactionDTO;
import com.enfocareservice.entity.Transaction;
import com.enfocareservice.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*") // ✅ Allows frontend to access API
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	// ✅ Fetch all transactions
	@GetMapping
	@ResponseBody
	public List<TransactionDTO> getAllTransactions() {
		List<Transaction> transactions = transactionService.getAllTransactions();
		return transactions.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// ✅ Save a transaction (for testing)
	@PostMapping
	public Transaction saveTransaction(@RequestBody Transaction transaction) {
		return transactionService.saveTransaction(transaction);
	}

	// ✅ Convert Entity to DTO for clean API responses
	private TransactionDTO convertToDTO(Transaction transaction) {
		return new TransactionDTO(transaction.getOrderId(), transaction.getEmail(), transaction.getAmount(),
				transaction.getCurrency(), transaction.getPaymentDate(), transaction.getPaymentMethod(),
				transaction.getSubscriptionType(), transaction.getReceiptUrl());
	}
}
