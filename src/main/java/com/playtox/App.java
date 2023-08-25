package com.playtox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class App {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 2;
        int accountCount = 4;
        int transferCount = 30;
        Run run = new Run();
        ExecutorService service = Executors.newFixedThreadPool(threadCount);
        List<Account> accountList = new ArrayList<>(Arrays.stream(new Account[accountCount]).toList());
        accountList.replaceAll(ignored -> new Account());
        List<Lock> lockList = new ArrayList<>(Arrays.stream(new ReentrantLock[accountList.size()]).toList());
        lockList.replaceAll(ignored -> new ReentrantLock());
        for (int i = 1; i <= transferCount; i++) {
            int transferNumber = i;
            service.submit(() -> {
                try {
                    run.makeTransfer(accountList, lockList, transferNumber);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.DAYS);
        run.printResult(accountList);
    }
}

class Run {
    private void takeLock(Lock lock1, Lock lock2) {
        boolean firstLockTaken = false;
        boolean secondLockTaken = false;
        while (true) {
            try {
                firstLockTaken = lock1.tryLock();
                secondLockTaken = lock2.tryLock();
            } finally {
                if (firstLockTaken && secondLockTaken) {
                    return;
                }

                if (firstLockTaken) {
                    lock1.unlock();
                }

                if (secondLockTaken) {
                    lock2.unlock();
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void makeTransfer(List<Account> accountList, List<Lock> lockList, int id) throws InterruptedException {
        Random random = new Random();
        int account1;
        int account2;
        do {
            account1 = random.nextInt(accountList.size());
            account2 = random.nextInt(accountList.size());
        } while (account1 == account2);
        takeLock(lockList.get(account1), lockList.get(account2));
        try {
            Account.transfer(accountList.get(account1), accountList.get(account2), random.nextInt(1000), id);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lockList.get(account1).unlock();
            lockList.get(account2).unlock();
            try {
                Thread.sleep(random.nextInt(1000) + 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void printResult(List<Account> accountList) {
        int result = 0;
        for (Account account : accountList) {
            int balance = account.getBalance();
            System.out.println("account" + account.getId() + ": " + balance);
            result += balance;
        }
        System.out.println("total " + result);
    }
}






