package com.playtox;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

class Run {
    private static final Logger logger = Logger.getLogger(Run.class);
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
            logger.info("account " + account.getId() + ": " + balance);
            result += balance;
        }
        logger.info("total :" + result);
    }
}
