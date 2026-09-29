package com.fintech.mobilemoney.controller;

import com.fintech.mobilemoney.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/wallets")
    public Iterable<?> getAllWallets() {
        return adminService.getAllWallets();
    }

    @GetMapping("/transactions")
    public Iterable<?> getAllTransactions() {
        return adminService.getAllTransactions();
    }

    @PostMapping("/wallet/{id}/freeze")
    public String freezeWallet(@PathVariable UUID id) {
        adminService.freezeWallet(id);
        return "Wallet " + id + " has been FROZEN.";
    }

    @PostMapping("/wallet/{id}/unfreeze")
    public String unfreezeWallet(@PathVariable UUID id) {
        adminService.unfreezeWallet(id);
        return "Wallet " + id + " has been UNFROZEN.";
    }
}