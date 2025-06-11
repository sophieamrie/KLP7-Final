package finalLab.Payment;

import finalLab.Model.User;

public class CreditCardPayment extends AbstractPayment implements ILoggable {

    public CreditCardPayment() {
        this.paymentType = "Credit Card";
    }

    @Override
    public boolean pay(User user, double amount) {

        logTransaction("Credit Card payment of " + amount + " was successful.");
        return true;
    }

    @Override
    public void logTransaction(String info) {
        System.out.println("[LOG] " + info);
    }
}