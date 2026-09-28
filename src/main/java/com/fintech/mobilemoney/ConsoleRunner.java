package com.fintech.mobilemoney;

import com.fintech.mobilemoney.model.Transaction;
import com.fintech.mobilemoney.service.UserService;
import com.fintech.mobilemoney.service.WalletService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

@Component
public class ConsoleRunner implements CommandLineRunner {

    private final UserService userService;
    private final WalletService walletService;

    public ConsoleRunner(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== WELCOME TO REAL MOBILE MONEY SYSTEM ===");
        System.out.print("Enter your phone number to register: ");
        String phone = scanner.nextLine();
        System.out.print("Enter your full name: ");
        String name = scanner.nextLine();
        System.out.print("Set a 4-digit PIN: ");
        String pin = scanner.nextLine();

        UUID walletId = userService.registerUser(phone, name, pin);
        System.out.println("\nRegistration Successful! Your Wallet ID is: " + walletId);
        
        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Exit");
            System.out.print("Choose an option (1-4): ");
            
            String choice = scanner.nextLine();
            
            try {
                switch (choice) {
                    case "1": 
                        BigDecimal bal = walletService.getBalance(walletId);
                        System.out.println("Current Balance: " + formatMoney(bal) + " (" + convertToWords(bal.doubleValue()) + " Only)");
                        break;
                        
                    case "2": 
                        System.out.print("Enter deposit amount: ");
                        BigDecimal depAmount = new BigDecimal(scanner.nextLine());
                        Transaction depReceipt = walletService.deposit(walletId, depAmount, UUID.randomUUID().toString());
                        printReceipt(depReceipt, walletService.getBalance(walletId), phone);
                        break;
                        
                    case "3": 
                        System.out.print("Enter withdrawal amount: ");
                        BigDecimal withAmount = new BigDecimal(scanner.nextLine());
                        Transaction withReceipt = walletService.withdraw(walletId, withAmount, UUID.randomUUID().toString());
                        printReceipt(withReceipt, walletService.getBalance(walletId), phone);
                        break;
                        
                    case "4": 
                        running = false;
                        System.out.println("Thank you for using Mobile Money. Goodbye!");
                        break;
                        
                    default:
                        System.out.println("Invalid option. Please choose 1-4.");
                }
            } catch (Exception e) {
                System.out.println("Transaction Failed: " + e.getMessage());
            }
        }
    }

    private String formatMoney(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }

    private void printReceipt(Transaction t, BigDecimal balance, String phone) {
        System.out.println("\n===========================================");
        System.out.println("          OFFICIAL TRANSACTION RECEIPT     ");
        System.out.println("===========================================");
        System.out.println("Transaction ID   : " + t.getId());
        System.out.println("Phone Number     : " + phone);
        System.out.println("Transaction Type : " + t.getType());
        System.out.println("Amount           : " + formatMoney(t.getAmount()));
        System.out.println("Amount in Words  : " + convertToWords(t.getAmount().doubleValue()) + " Only");
        System.out.println("Transaction Charge: " + formatMoney(t.getFee()));
        System.out.println("Updated Balance  : " + formatMoney(balance));
        System.out.println("Status           : " + t.getStatus());
        System.out.println("===========================================\n");
    }

    // FIXED: Robust method to convert large numbers to words
    private String convertToWords(double number) {
        if (number == 0) return "Zero";

        String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        long num = (long) number;
        StringBuilder words = new StringBuilder();

        if (num >= 1000000) {
            words.append(convertChunk(num / 1000000, ones, tens)).append(" Million ");
            num %= 1000000;
        }
        if (num >= 1000) {
            words.append(convertChunk(num / 1000, ones, tens)).append(" Thousand ");
            num %= 1000;
        }
        if (num > 0) {
            words.append(convertChunk(num, ones, tens));
        }

        return words.toString().trim();
    }

    // Helper to handle chunks of numbers (Hundreds, Tens, Ones)
    private String convertChunk(long number, String[] ones, String[] tens) {
        StringBuilder chunk = new StringBuilder();
        if (number >= 100) {
            chunk.append(ones[(int)(number / 100)]).append(" Hundred ");
            number %= 100;
        }
        if (number >= 20) {
            chunk.append(tens[(int)(number / 10)]);
            if (number % 10 > 0) {
                chunk.append(" ").append(ones[(int)(number % 10)]);
            }
        } else if (number > 0) {
            chunk.append(ones[(int) number]);
        }
        return chunk.toString().trim();
    }
}