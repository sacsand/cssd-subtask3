/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;

/**
 *
 * @author Ravi
 */
public class Packages implements Serializable{
    private float peakCharge;
    private float offPeakCharge;
    private float amountNeeded;
    private String packageId;

    public Packages(float peakCharge, float offPeakCharge, float amountNeeded, String packageId) {
        this.peakCharge = peakCharge;
        this.offPeakCharge = offPeakCharge;
        this.amountNeeded = amountNeeded;
        this.packageId = packageId;
    }
    

    public float getPeakCharge() {
        return peakCharge;
    }

    public void setPeakCharge(float peakCharge) {
        this.peakCharge = peakCharge;
    }

    public void setOffPeakCharge(float offPeakCharge) {
        this.offPeakCharge = offPeakCharge;
    }

    public void setAmountNeeded(float amountNeeded) {
        this.amountNeeded = amountNeeded;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public float getOffPeakCharge() {
        return offPeakCharge;
    }

    public float getAmountNeeded() {
        return amountNeeded;
    }

    public String getPackageId() {
        return packageId;
    }
    
}
