package vaultx_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vaultx_backend.entity.Transaction;

import java.util.Arrays;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, String> {

    List<Transaction> findByAccountNumberOrderByTimestampDesc(
            Long accountNumber
    );

    List<Transaction> findTop5ByAccountNumberOrderByTimestampDesc(Long accountNumber);

    List<Transaction> findByAccountNumberAndTransactionType(
            Long accountNumber,
            String transactionType
    );
}