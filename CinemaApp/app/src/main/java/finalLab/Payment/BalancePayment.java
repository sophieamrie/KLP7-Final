package finalLab.Payment;

import finalLab.Model.User;

public class BalancePayment extends AbstractPayment implements ILoggable {

    public BalancePayment() {
        super();
        this.paymentType = "Balance";
    }

    @Override
    public boolean pay(User user, double amount) {
        if (user.getBalance() >= amount) {
            user.setBalance(user.getBalance() - amount);
            logTransaction("Payment of " + amount + " was successful.");
            return true;
        }
        logTransaction("Payment of " + amount + " failed. Not enough balance.");
        return false;
    }

    @Override
    public void logTransaction(String info) {
        System.out.println("[LOG] " + info);
    }
}
