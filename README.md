# 💳 Mobile Money Transaction System

A production-grade, secure, and scalable fintech application built with Java Spring Boot. This system allows users to register, manage digital wallets, perform deposits and withdrawals (with automated fee calculations), and view professional transaction receipts. It also features a comprehensive Admin Control Panel for system oversight and security management.

## 🌟 Key Features

### 👤 User Dashboard
- **Secure Authentication**: User registration and login with encrypted PINs.
- **Wallet Management**: Real-time balance tracking.
- **Transactions**: 
  - Instant Deposits.
  - Withdrawals with an automated **1.5% transaction fee**.
- **Professional Receipts**: Generates detailed receipts including Transaction ID, Fees, Updated Balance, and **Amount in Words** (supports large numbers up to millions).

### 🛡️ Admin Control Panel
- **Live Statistics**: Real-time overview of total wallets, system balance, and transaction volume.
- **Audit Trail**: Complete, searchable history of all transactions with timestamps and descriptions.
- **Security Controls**: Ability to instantly **Freeze** or **Unfreeze** user wallets to prevent unauthorized access.

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot, Spring Data JDBC, Spring Security
- **Database**: PostgreSQL (Hosted on Neon Serverless)
- **Frontend**: HTML5, CSS3, Vanilla JavaScript (No heavy frameworks, ensuring lightning-fast load times)
- **Build Tool**: Maven

## 🚀 How to Run Locally

### Prerequisites
- Java 17 or higher
- Maven
- A PostgreSQL database (e.g., [Neon.tech](https://neon.tech))

### Setup Instructions
1. **Clone the repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/mobile-money-system.git
   cd mobile-money-system