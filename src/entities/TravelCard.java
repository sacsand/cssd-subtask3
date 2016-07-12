/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Ravi
 */
public class TravelCard implements Serializable {

    private Date expireDate;
    private int pinNumber;
    private String packageType;
    private String acountNumber;
    private String cardId;
    private static int cardCount = 0;
    
    
     public TravelCard(Date expireDate, int pinNumber, String packageType, String acountNumber) {
        this.expireDate = expireDate;
        this.pinNumber = pinNumber;
        this.packageType = packageType;
        this.acountNumber = acountNumber;
        this.cardId="SC"+cardCount;
        cardCount++;
    }
     public TravelCard(Date expireDate, int pinNumber, String acountNumber) {
        this.expireDate = expireDate;
        this.pinNumber = pinNumber;
        this.acountNumber = acountNumber;
        this.cardId="SC"+cardCount;
        cardCount++;
    }
    //for passive cards
    public TravelCard(Date expireDate, String acountNumber) {
        this.expireDate = expireDate;
        this.acountNumber = acountNumber;
        this.cardId="PC"+cardCount;
        cardCount++;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public String getPackageType() {
        return packageType;
    }

    public String getAcountNumber() {
        return acountNumber;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void setPinNumber(int pinNumber) {
        this.pinNumber = pinNumber;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public void setAacountNumber(String acountNumber) {
        this.acountNumber = acountNumber;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException //to serialize the static variable bookCount
    {
        out.defaultWriteObject();
        out.writeObject(new Integer(cardCount));
    }

    //cheeath
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException//to deserialize the static variable bookCount
    {
        in.defaultReadObject();
        cardCount = (Integer) in.readObject();
    }

    public String getCardId() {
        return cardId;
    }
    
    
    
}
