package org.example.nbcamchapter5;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.example.nbcamchapter5.common.entity.Account;
import org.example.nbcamchapter5.domain.account.repository.AccountRepository;
import org.example.nbcamchapter5.domain.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PessimisticLockTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @Test
    void 동시에_두_트랜잭션_출금_테스트() {
        // 초기 데이터 세팅
        Account account = accountRepository.save(new Account(100));

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable task1 = () -> {
            try {
                accountService.withdraw(account.getId(), 10);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        Runnable task2 = () -> {
            try {
                accountService.withdraw(account.getId(), 10);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        Runnable task3 = () -> {
            try {
                accountService.withdraw(account.getId(), 10);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        // 동시에 실행
        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        executor.shutdown();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

        // 결과 검증
        Account result = accountRepository.findById(account.getId()).orElseThrow();
        System.out.println("최종 잔액: " + result.getBalance());
    }

    @Test
    void 동시에_두_트랜잭션_출금_테스트_락없음() throws InterruptedException {
        // 초기 데이터 세팅
        Account account = accountRepository.save(new Account(100));

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable task1 = () -> {
            try {
                accountService.withdrawNoLock(account.getId(), 10);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        Runnable task2 = () -> {
            try {
                accountService.withdrawNoLock(account.getId(), 10);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        Runnable task3 = () -> {
            try {
                accountService.withdrawNoLock(account.getId(), 10);
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + " 실패: " + e.getMessage());
            }
        };

        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        executor.shutdown();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Account result = accountRepository.findById(account.getId()).orElseThrow();
        System.out.println("최종 잔액: " + result.getBalance());
    }
}
