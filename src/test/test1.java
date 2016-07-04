package test;


import controllers.AccountControl;
import entities.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Anuradha
 */
public class test1 {

    
    public static void main(String args[]) throws IOException, ParseException, ClassNotFoundException{
        
        String dateStr = "06/27/2007";
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = formatter.parse(dateStr); 
        
        //MobileAccount acc=new MobileAccount("M123456",100,startDate,startDate,"one day pass",2,4,5,66);
        //Account ac=new Account("456789");
        //AC.createTempAccount(acc);
        //AC.add(ac);
        //AccountControl.createMobileAccount(acc);
        //AccountControl.serialize();
        AccountControl.deserialize();
  
        
        MobileAccount temp=AccountControl.findMobileAccountByAccountNumber("M123456");
        
        int cnum=temp.getCreditCardNumber();
        System.out.println(cnum);
            
         
        
        
        
    }

 
 }

