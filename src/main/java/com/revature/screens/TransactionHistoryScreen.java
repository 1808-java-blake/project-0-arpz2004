package com.revature.screens;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.revature.beans.Transaction;
import com.revature.beans.User;
import com.revature.daos.TransactionDao;
import com.revature.helpers.BigDecimalHelper;

public class TransactionHistoryScreen implements Screen {
	private Scanner scan = new Scanner(System.in);
	private TransactionDao td = TransactionDao.currentTransactionDao;

	@Override
	public Screen start() {
		User u = User.getCurrentUser();
		List<Integer> transactionHistory = u.getTransactionHistory();
		List<String> transactionAmounts = new ArrayList<>();
		for (Integer transactionID : transactionHistory) {
			Transaction transaction = td.findByUserAndID(u, transactionID);
			transactionAmounts.add(BigDecimalHelper.getMoneyString(transaction.getAmount()));
		}
		int lastTransactionID = 0;
		if (!transactionHistory.isEmpty()) {
			lastTransactionID = transactionHistory.get(transactionHistory.size() - 1);
		}
		int maxIDLength = String.valueOf(lastTransactionID).length();
		int maxAmountLength = maxLengthString(transactionAmounts);
		int dateLength = 7;
		if (transactionHistory.size() > 0) {
			dateLength = 22;
		}
		int amountLength = Math.max(9, maxAmountLength + 3);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		System.out.println(padRight("#", maxIDLength + 3) + padRight("Time", dateLength)
				+ padRight("Amount", amountLength) + "Balance");
		BigDecimal balance = new BigDecimal(0);
		for (Integer transactionID : transactionHistory) {
			Transaction transaction = td.findByUserAndID(u, transactionID);
			balance = balance.add(transaction.getAmount());
			String transactionTime = transaction.getTime().format(formatter);
			System.out.println(padRight(String.valueOf(transaction.getTransactionID()), maxIDLength + 3)
					+ padRight(transactionTime, dateLength) + padRight(transaction.getAmountString(), amountLength)
					+ BigDecimalHelper.getMoneyString(balance));
		}
		System.out.println("Press enter to return to home screen.");
		scan.nextLine();
		return new HomeScreen();
	}

	private String padRight(String oldString, int newLength) {
		return String.format("%1$-" + newLength + "s", oldString);
	}

	private int maxLengthString(List<String> amounts) {
		int maxLength = 0;
		for (String amount : amounts) {
			int amountLength = amount.length();
			if (amountLength > maxLength) {
				maxLength = amountLength;
			}
		}
		return maxLength;
	}
}