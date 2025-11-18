package org.example.nbcamchapter5.domain.account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.nbcamchapter5.common.entity.Account;
import org.example.nbcamchapter5.domain.account.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public void withdraw(Long accountId, int amount) {
        Account account = accountRepository.findByIdForLOCK(accountId); // 🔒 락 획득
        System.out.println(Thread.currentThread().getName() + " → 락 획득 완료");

        account.decrease(amount);
        System.out.println(Thread.currentThread().getName() + " → 출금 완료 (잔액: " + account.getBalance() + ")");
    }

    @Transactional
    public void withdrawNoLock(Long accountId, int amount) {

        Account account = accountRepository.findById(accountId).orElseThrow(); // 🔒 락 획득

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        account.decrease(amount);
        System.out.println(Thread.currentThread().getName() + " → 출금 완료 (잔액: " + account.getBalance() + ")");
    }
}

