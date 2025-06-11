package finalLab.Payment;

import finalLab.Model.User;

public class EWalletPayment extends AbstractPayment implements ILoggable {

    public EWalletPayment() {
        this.paymentType = "E-Wallet";
    }

    @Override
    public boolean pay(User user, double amount) {
        if (user.getEWalletBalance() >= amount) {
            user.setEWalletBalance(user.getEWalletBalance() - amount);
            logTransaction("E-Wallet payment of " + amount + " was successful.");
            return true;
        }
        logTransaction("E-Wallet payment of " + amount + " failed. Not enough balance.");
        return false;
    }

    @Override
    public void logTransaction(String info) {
        System.out.println("[LOG] " + info);
    }
}