package finalLab.Payment;

import finalLab.Model.User;

public class DebitPayment extends AbstractPayment implements ILoggable {

    public DebitPayment() {
        this.paymentType = "Cash";
    }

    @Override
    public boolean pay(User user, double amount) {
        logTransaction("Cash payment of " + amount + " was accepted.");
        return true;
    }

    @Override
    public void logTransaction(String info) {
        System.out.println("[LOG] " + info);
    }
}