import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Bank {
    private final Random random = new Random();
    private final ConcurrentHashMap<String, Account> accounts;

    public Bank() {
        accounts = new ConcurrentHashMap<>();
    }

    void addAccount(String numberAccount, long money) {
        accounts.put(numberAccount, new Account(numberAccount, money));
    }

    Account getAccount(String number) {
        return accounts.get(number);
    }

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException {
        long moneyFrom = accounts.get(fromAccountNum).getMoney();
        long moneyTo = accounts.get(toAccountNum).getMoney();

        if (accounts.get(fromAccountNum).isBlock()) {
            System.out.println("Аккаунт " + accounts.get(fromAccountNum).getAccNumber() + " заблокирован. Транзакция невозможна");
            return;
        }
        if (accounts.get(toAccountNum).isBlock()) {
            System.out.println("Аккаунт " + accounts.get(toAccountNum).getAccNumber() + " заблокирован. Транзакция невозможна");
            return;
        }
        if (moneyFrom > amount) {
            if (amount > 50000) {
                if (isFraud(fromAccountNum, toAccountNum, amount)) {
                    System.out.println("Это мошенники!!! Оба счета заблокированы!!!");
                    accounts.get(fromAccountNum).blockingAccount();
                    accounts.get(toAccountNum).blockingAccount();
                } else {
                    synchronized (accounts) {
                        accounts.get(fromAccountNum).setMoney(moneyFrom - amount);
                        accounts.get(toAccountNum).setMoney(moneyTo + amount);
                    }
                }
            }
        } else {
            System.out.println("Недостадочно денег на счете");
        }

    }

    public long getBalance(String accountNum) {
        return accounts.get(accountNum).getMoney();
    }

}
