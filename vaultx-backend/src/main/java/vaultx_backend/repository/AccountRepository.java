package vaultx_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaultx_backend.entity.Account;

import java.util.Optional;

public interface AccountRepository
        extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(Long accountNumber);
}