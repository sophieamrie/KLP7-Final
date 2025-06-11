package finalLab.Payment;

import finalLab.Model.User;

public abstract class AbstractPayment implements IPayment {
    protected String paymentType;

    public abstract boolean pay(User user, double amount);
    
    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String generateReceipt(User user, double amount) {
        return "Receipt: " + paymentType + ", User: " + user.getUsername() + ", Amount: " + amount;
    }
}