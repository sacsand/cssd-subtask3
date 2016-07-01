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
public class Account implements Serializable {
    
   private String accountNumber;
   private float amount;
   private Date createdDate;
   private Date expireDate;
   private String packageType;

    public Account(String accountNumber, float amount, Date createdDate, Date expireDate, String packageType) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.createdDate = createdDate;
        this.expireDate = expireDate;
        this.packageType = packageType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public float getAmount() {
        return amount;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }
    
    public void changePackage(String packageType){
        setPackageType(packageType);
    }
   
    
    
}
