/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Ravi
 */
public class MobileAccount extends Account implements Serializable{
    private int payPalAccount;
    private int creditCardNumber;
    private int debitCardNumber;
    private int subscriberId;

    public MobileAccount(int subscriberId,Date createdDate) {
        super(createdDate);
        this.subscriberId = subscriberId;
    }
    public MobileAccount(String accountNumber, float amount, Date createdDate, Date expireDate, String packageType,int payPalAccount, int creditCardNumber,int debitCardNumber,int subscriberId) {
        super(accountNumber, amount, createdDate, expireDate, packageType);
        
        this.payPalAccount=payPalAccount;
        this.creditCardNumber=creditCardNumber;
        this.debitCardNumber=debitCardNumber;
        this.subscriberId=subscriberId;
        
    }

    public int getPayPalAccount() {
        return payPalAccount;
    }

    public int getCreditCardNumber() {
        return creditCardNumber;
    }

    public int getDebitCardNumber() {
        return debitCardNumber;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setPayPalAccount(int payPalAccount) {
        this.payPalAccount = payPalAccount;
    }

    public void setCreditCardNumber(int creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public void setDebitCardNumber(int debitCardNumber) {
        this.debitCardNumber = debitCardNumber;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }
    
   public void topUp(float amount) {
        super.setAmount(super.getAmount() + amount);

    }

    public void addAmount(float amount) {
        super.setAmount(amount);

    }

    public void deductAmount(float amount) {
        super.setAmount(super.getAmount() - amount);

    }
    
    
}
