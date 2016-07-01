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
public class TravelCard implements Serializable {

    public TravelCard(Date expireDate, String pinNumber, String packageType, String acountNumber) {
        this.expireDate = expireDate;
        this.pinNumber = pinNumber;
        this.packageType = packageType;
        this.acountNumber = acountNumber;
    }
    private Date expireDate;
    private String pinNumber;
    private String packageType;
    private String acountNumber;

    public Date getExpireDate() {
        return expireDate;
    }

    public String getPinNumber() {
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

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public void setAacountNumber(String acountNumber) {
        this.acountNumber = acountNumber;
    }
    
    
    
    
    
}
