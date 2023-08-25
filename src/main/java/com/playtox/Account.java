package com.playtox;

import org.apache.log4j.Logger;

import java.util.Random;

public class Account {

    private final int id;
    private int balance;

    public Account() {
        balance = 10000;
        id = new Random().nextInt(100);
    }


    public void deposit(int amount) {
        balance += amount;
    }

    public boolean withdraw(int amount) {
        if (balance - amount > 0) {
            balance -= amount;
            return true;
        } else {
            return false;
        }

    }

    public int getBalance() {
        return balance;
    }

    public static void transfer(Account acc1, Account acc2, int amount, int id) throws InterruptedException {
        final Logger logger = Logger.getLogger(Account.class);
        if (!acc1.withdraw(amount)) {
            logger.info(String.format("Transfer number: %d sending amount: %d from account: %d to account: %d Failed", id, amount, acc1.getId(), acc2.getId()));
            return;
        }
        acc2.deposit(amount);
        logger.info(String.format("Transfer number: %d sending amount: %d from account: %d to account: %d Success", id, amount, acc1.getId(), acc2.getId()));


    }

    public int getId() {
        return id;
    }
}
