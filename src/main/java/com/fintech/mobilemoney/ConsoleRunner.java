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

    // Helper to format money cleanly (removes ugly .0000 trailing zeros)
    private String formatMoney(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }

    // Upgraded Receipt Printer
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

    // Helper method to convert numbers to words
    private String convertToWords(double number) {
        if (number == 0) return "Zero";
        
        String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        
        long num = (long) number;
        StringBuilder words = new StringBuilder();
        
        if (num >= 1000000) {
            words.append(ones[(int)(num / 1000000)]).append(" Million ");
            num %= 1000000;
        }
        if (num >= 1000) {
            words.append(ones[(int)(num / 1000)]).append(" Thousand ");
            num %= 1000;
        }
        if (num >= 100) {
            words.append(ones[(int)(num / 100)]).append(" Hundred ");
            num %= 100;
        }
        if (num > 0) {
            if (num < 20) words.append(ones[(int) num]);
            else {
                words.append(tens[(int)(num / 10)]);
                if (num % 10 > 0) words.append(" ").append(ones[(int)(num % 10)]);
            }
        }
        
        return words.toString().trim();
    }
}