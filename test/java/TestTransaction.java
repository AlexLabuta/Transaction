import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTransaction {
    Bank bank;
    Bank lockedAccountBank;

    @BeforeEach
    public void init() {
        bank = new Bank();
        for (int i = 1; i < 11; i++) {
            bank.addAccount(String.valueOf(i), 500_000);
        }
        lockedAccountBank = new Bank();
        for (int i = 1; i < 11; i++) {
            lockedAccountBank.addAccount(String.valueOf(i), 500_000);
            lockedAccountBank.getAccount(String.valueOf(i)).setBlock();
        }
    }

    @DisplayName("Если аккаунт заблокирован")
    @Test
    public void transferIfAccountIsBlockedTest() throws InterruptedException {

        long beginMoneyFrom = lockedAccountBank.getAccount("1").getMoney();
        long beginMoneyTo = bank.getAccount("1").getMoney();

        bank.transfer(bank.getAccount("1").getAccNumber(), lockedAccountBank.getAccount("1").getAccNumber(), 1000);
        assertAll("На обоих счетах баланс не должен измениться",
                () -> assertEquals(lockedAccountBank.getAccount("1").getMoney(), beginMoneyFrom),
                () -> assertEquals(bank.getAccount("1").getMoney(), beginMoneyTo)
        );
    }

    @DisplayName("Перевод в мультипоточности")
    @Test
    void transferMultiThreadingTest() throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    bank.transfer("1", "2", 60_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(String.format("Balance of account #1 is %s and #2 is %s", bank.getBalance("2"),
                        bank.getBalance("1")));

            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @DisplayName("Тест Deadlock")
    @Test
    public void testDeadLock() throws InterruptedException {

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 10000; j++) {
                        bank.transfer("3", "4", 300);
                        bank.transfer("4", "3", 500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
