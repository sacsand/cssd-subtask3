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
public class TempAccount extends Account implements Serializable{
    private String area;
    private String cardId;

    public TempAccount(String accountNumber, float amount, Date createdDate, Date expireDate, String packageType,String area,String cardId) {
        super(accountNumber, amount, createdDate, expireDate, packageType);
        
        this.area=area;
        this.cardId=cardId;
    }
    public TempAccount(Date createdDate, Date expireDate) {
        super(createdDate, expireDate);
    }

    public String getArea() {
        return area;
    }

    public String getCardId() {
        return cardId;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
    
    
    
}
