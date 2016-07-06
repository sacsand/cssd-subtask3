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
public class Account implements Serializable {

    private String accountNumber;
    private double amount;
    private Date createdDate;
    private Date expireDate;
    private String packageType;
    private static int accountCount = 0; //cheetah

    
    //cheetah
    public Account(Date createdDate) {
        this.accountNumber = "M" + accountCount;

        this.createdDate = createdDate;
        this.expireDate = new Date(createdDate.getTime() + ((24 * 60 * 60 * 1000) * 1000));
        this.packageType = packageType;
        accountCount++; //increment the accountCount

    }

    public Account(String accountNumber, double amount, Date createdDate, Date expireDate, String packageType) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.createdDate = createdDate;
        this.expireDate = expireDate;
        this.packageType = packageType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
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

    public void setAmount(double amount) {
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

    public void changePackage(String packageType) {
        setPackageType(packageType);
    }
    //cheetah
    private void writeObject(ObjectOutputStream out) throws IOException //to serialize the static variable bookCount
    {
        out.defaultWriteObject();
        out.writeObject(new Integer(accountCount));
    }
    //cheeath
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException//to deserialize the static variable bookCount
    {
        in.defaultReadObject();
        accountCount = (Integer) in.readObject();
    }
    //cheetah
    public static int getAccountCount() {
        return accountCount;
    }

}
