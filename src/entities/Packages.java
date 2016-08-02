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

/**
 *
 * @author Ravi
 */
public class Packages implements Serializable{
    private String packageName;
    private Double peakCharge;
    private Double offPeakCharge;
    private Double amountNeeded;
    private String packageId;
    private static int packageCount = 0;
    public Packages(String packageName,Double peakCharge, Double offPeakCharge, Double amountNeeded) {
        this.packageName = packageName;
        this.peakCharge = peakCharge;
        this.offPeakCharge = offPeakCharge;
        this.amountNeeded = amountNeeded;
        this.packageId = "P" + packageCount;
        packageCount++;
    }
    
    
    
    public String  getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    
    
    public double getPeakCharge() {
        return peakCharge;
    }

    public void setPeakCharge(double peakCharge) {
        this.peakCharge = peakCharge;
    }

    public void setOffPeakCharge(double offPeakCharge) {
        this.offPeakCharge = offPeakCharge;
    }

    public void setAmountNeeded(double amountNeeded) {
        this.amountNeeded = amountNeeded;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public double getOffPeakCharge() {
        return offPeakCharge;
    }

    public double getAmountNeeded() {
        return amountNeeded;
    }

    public String getPackageId() {
        return packageId;
    }
    
    //added by vish
    public static int getPackageCount() {
        return packageCount;
    }
    
    //to serialize the static variable packageCount
      private void writeObject(ObjectOutputStream out) throws IOException 
    {
        out.defaultWriteObject();
        out.writeObject(new Integer(packageCount));
    }
   
    //to deserialize the static variable packageCount
         private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException
    {
        in.defaultReadObject();
        packageCount = (Integer) in.readObject();
    }
}
