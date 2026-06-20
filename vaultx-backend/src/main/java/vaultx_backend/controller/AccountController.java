package vaultx_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vaultx_backend.entity.Account;
import vaultx_backend.repository.AccountRepository;

@RestController
@RequestMapping("/api/account")
@CrossOrigin("*")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/balance/{accountNumber}")
    public Double getBalance(
            @PathVariable Long accountNumber
    ) {

        Account account =
                accountRepository
                        .findById(accountNumber)
                        .orElse(null);

        if (account == null) {
            return -1.0;
        }

        return account.getBalance();
    }
}