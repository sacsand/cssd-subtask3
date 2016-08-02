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
public class SmartAccount extends Account implements Serializable{

    private String cardId;

    public SmartAccount(Date createdDate, Date expireDate) {
        super(createdDate, expireDate);
    }
    public SmartAccount(String accountNumber, float amount, Date createdDate, Date expireDate, String packageType,String cardId) {
        super(accountNumber, amount, createdDate, expireDate, packageType);
        this.cardId=cardId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public void topUp(double amount) {
        super.setAmount(super.getAmount() + amount);

    }

    public void addAmount(float amount) {
        super.setAmount(amount);

    }

    public void deductAmount(Double amount) {
        super.setAmount(super.getAmount() - amount);

    }

}
