package vaultx_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vaultx_backend.dto.DepositRequest;
import vaultx_backend.dto.WithdrawRequest;
import vaultx_backend.entity.Account;
import vaultx_backend.entity.Transaction;
import vaultx_backend.entity.User;
import vaultx_backend.repository.AccountRepository;
import vaultx_backend.repository.TransactionRepository;
import vaultx_backend.repository.UserRepository;
import vaultx_backend.service.OtpService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transaction")
@CrossOrigin("*")
public class TransactionController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;


    @PostMapping("/deposit")
    public String deposit(@RequestBody DepositRequest request) {

        System.out.println("ACCOUNT: " + request.getAccountNumber());
        System.out.println("AMOUNT: " + request.getAmount());

        if (request.getAccountNumber() == null || request.getAmount() == null) {
            return "Invalid request data";
        }

        Account account = accountRepository.findById(request.getAccountNumber()).orElse(null);

        if (account == null) {
            return "Account not found";
        }

        if (request.getAmount() <= 0) {
            return "Invalid amount";
        }

        Double currentBalance = account.getBalance();
        if (currentBalance == null) {
            currentBalance = 0.0;
        }

        account.setBalance(currentBalance + request.getAmount());
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(request.getAmount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");
        transaction.setDescription("Amount Deposited");
        transaction.setAccountNumber(account.getAccountNumber());

        transactionRepository.save(transaction);
        User user = userRepository
                .findByAccountNumber(account.getAccountNumber())
                .orElse(null);

        if(user != null){

            otpService.sendTransactionMessage(
                    "91" + user.getPhone(),
                    "credited",
                    request.getAmount(),
                    account.getBalance()
            );
        }

        return "Deposit Successful";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestBody WithdrawRequest request) {

        Account account = accountRepository
                .findById(request.getAccountNumber())
                .orElse(null);

        if (account == null) {
            return "Account not found";
        }

        if (account.getBalance() < request.getAmount()) {
            return "Insufficient Balance";
        }

        account.setBalance(
                account.getBalance() - request.getAmount()
        );

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setTransactionType("WITHDRAW");
        transaction.setAmount(request.getAmount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");
        transaction.setDescription("Amount Withdrawn");
        transaction.setAccountNumber(account.getAccountNumber());

        transactionRepository.save(transaction);

        User user = userRepository
                .findByAccountNumber(account.getAccountNumber())
                .orElse(null);

        if (user != null) {

            otpService.sendTransactionMessage(
                    "91" + user.getPhone(),
                    "debited",
                    request.getAmount(),
                    account.getBalance()
            );
        }

        return "Withdrawal Successful";
    }


    @GetMapping("/balance/{accountNumber}")
    public Double getBalance(@PathVariable Long accountNumber) {

        Account account = accountRepository
                .findById(accountNumber)
                .orElse(null);

        if (account == null) {
            return 0.0;
        }

        return account.getBalance();
    }


    @GetMapping("/history/{accountNumber}")
    public List<Transaction> getHistory(
            @PathVariable Long accountNumber) {

        return transactionRepository
                .findTop5ByAccountNumberOrderByTimestampDesc(
                        accountNumber
                );
    }

    @GetMapping("/total-deposit/{accountNumber}")
    public Double totalDeposit(@PathVariable Long accountNumber) {

        return transactionRepository
                .findByAccountNumberAndTransactionType(
                        accountNumber,
                        "DEPOSIT"
                )
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    @GetMapping("/total-withdraw/{accountNumber}")
    public Double totalWithdraw(@PathVariable Long accountNumber) {

        return transactionRepository
                .findByAccountNumberAndTransactionType(
                        accountNumber,
                        "WITHDRAW"
                )
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

}
