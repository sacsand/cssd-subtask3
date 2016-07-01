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
   
    public static Vector<MobileAccount> setOfMobileAccounts=new Vector<MobileAccount>(); 
    
    public static void createMobileAccount(MobileAccount account){
           setOfMobileAccounts.add(account);
    }
    public static void serialize() throws IOException{
      Serialization.serialize(setOfMobileAccounts, "mobileAccounts.ser");
    }
    public static Vector<MobileAccount> deserialize() throws IOException, ClassNotFoundException{
      return (Vector<MobileAccount>) Serialization.deserialize("mobileAccounts.ser");
    }
 
}
