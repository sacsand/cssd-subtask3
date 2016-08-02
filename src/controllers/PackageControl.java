/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.*;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.Vector;

/**
 *
 * @author Anuradha
 */
public class PackageControl implements Serializable {
    
     private volatile static PackageControl uniqueInstance;
    public static synchronized PackageControl getInstance()
  {
      if(uniqueInstance==null)
      {
          synchronized(PackageControl.class)
          {
              if(uniqueInstance==null)
                uniqueInstance=new PackageControl();
          }
      }
      return uniqueInstance;
  }
    private PackageControl(){
        
    }
    private static Vector<Packages> setOfPackages= new Vector<Packages>();
    
    public void createPackage(Packages pack){
        setOfPackages.add(pack);
    }
    public void deletePackage(Packages pack){
        
    }
    /*
    public Packages findPackageByPackageID(String packageID){
         for(int i=0;i<setOfPackages.size();i++)
            {
               for(Packages PA: setOfPackages)
                if((PA.getPackageId()).equals(packageID))
                {
                    return PA;
                }else
                {
                    return null;
                }
            }
        return null;
     }*/
   //added by vish
 public Packages findPackageByPackageID(String packageID){ 
    Packages pk=null; 
        for(Packages pack:setOfPackages){
            if(pack.getPackageId().equalsIgnoreCase(packageID))
            {
                pk=pack;
            }     
        }
      return pk ;
 }
    public double getChargesByTime(Packages pack,String time){
        
        String[] parts = time.split("(?=:)");
        int Time =Integer.parseInt(parts[0]); 
        
        if(Time>0 || Time<7){
            return pack.getOffPeakCharge();
        }
        else{
            return pack.getPeakCharge();
        }
    }
    //added by vish 
    public Vector<Packages> getAllPackages(){
    
    return setOfPackages;
    
    }
    
    
     public static void serialize() throws IOException{
      Serialization.serialize(setOfPackages, "packages.ser");
     }
    public static void deserialize() throws IOException, ClassNotFoundException{
       setOfPackages=(Vector<Packages>) Serialization.deserialize("packages.ser");
    }
}
