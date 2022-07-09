public class Account {
    private long money;
    private final String accNumber;
    private boolean block = false;

    public Account(String accNumber, long money) {
        this.money = money;
        this.accNumber = accNumber;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        if (block) {
            System.out.println(accNumber + " заблокирован");
        } else {
            this.money = money;
        }
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void blockingAccount() {
        block = true;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock() {
        block = true;
    }
}
