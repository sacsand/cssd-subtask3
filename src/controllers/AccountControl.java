/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
/**
 *
 * @author Anuradha
 */
public class AccountControl implements Serializable {
    
    private volatile static AccountControl uniqueInstance;
    public static synchronized AccountControl getInstance()
  {
      if(uniqueInstance==null)
      {
          synchronized(AccountControl.class)
          {
              if(uniqueInstance==null)
                uniqueInstance=new AccountControl();
          }
      }
      return uniqueInstance;
  }
   private AccountControl(){
   }
    private static Vector<MobileAccount> setOfMobileAccounts=new Vector<MobileAccount>(); 
    private static Vector<TempAccount> setOfTempAccounts=new Vector<TempAccount>();
    private static Vector<SmartAccount> setOfSmartAccounts=new Vector<SmartAccount>();
    
    
    public static void createTempeAccount(TempAccount account){
           setOfTempAccounts.add(account);
    }
    public static void createMobileAccount(MobileAccount account){
           setOfMobileAccounts.add(account);
    }
     public static void createSmartAccount(SmartAccount account){
           setOfSmartAccounts.add(account);
    }
     
     
     public static MobileAccount findMobileAccountByAccountNumber(String AccNo){
        MobileAccount mobileAc=null;
               for(MobileAccount MA: setOfMobileAccounts )
               {
                if((MA.getAccountNumber()).equals(AccNo))
                {
                    return MA;
                }else
                {
                    mobileAc=null;
                }
               }
            return mobileAc;
     }
     /**
     * @author ravi
     * @param mobile
     * @return mobile account object find a mobile account by mobile number
     */
    public static MobileAccount findMobileAccountByMobileNumber(int mobileNumber) {

        MobileAccount mAcc = null;
        for (MobileAccount MA : setOfMobileAccounts) {

            if ((MA.getSubscriberId() == mobileNumber)) {

                return MA;

            } else {
                mAcc = null;
            }
        }
        return mAcc;
    }
     public static SmartAccount findSmartAccountByAccountNumber(String AccNo){
        SmartAccount smartAc=null;   
               for(SmartAccount SA: setOfSmartAccounts )
               {
                if((SA.getAccountNumber()).equals(AccNo))
                {
                    return SA;
                }else
                {
                    smartAc=null;
                }
               }
            return smartAc;
     }
      public static TempAccount findTemptAccountByAccountNumber(String AccNo){
            
               for(TempAccount TA: setOfTempAccounts )
                if((TA.getAccountNumber()).equals(AccNo))
                {
                    return TA;
                }else
                {
                    return null;
                }
            return null;
     }
    public static void serialize() throws IOException{
      Serialization.serialize(setOfMobileAccounts, "mobileAccounts.ser");
      Serialization.serialize(setOfTempAccounts, "tempAccounts.ser");
      Serialization.serialize(setOfSmartAccounts, "smartAccounts.ser");
    }
    public static void deserialize() throws IOException, ClassNotFoundException{
       setOfMobileAccounts=(Vector<MobileAccount>) Serialization.deserialize("mobileAccounts.ser");
       setOfTempAccounts=(Vector<TempAccount>) Serialization.deserialize("tempAccounts.ser");
       setOfSmartAccounts=(Vector<SmartAccount>) Serialization.deserialize("smartAccounts.ser");
    }
 
}
