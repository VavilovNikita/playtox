package com.playtox;

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
        if (!acc1.withdraw(amount)){
            System.out.println("Transfer number " + id + " Failed");
            return;
        }
        acc2.deposit(amount);
        System.out.println("Transfer number " + id + " sucsess");
    }

    public int getId() {
        return id;
    }
}
