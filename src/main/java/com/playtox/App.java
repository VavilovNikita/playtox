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








