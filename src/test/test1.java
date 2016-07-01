package test;


import controllers.AccountControl;
import entities.MobileAccount;
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
    //private static AccountControl AC=new AccountControl();
    public static Vector<MobileAccount> mobileAccounts=new Vector<MobileAccount>();
    
    public static void main(String args[]) throws IOException, ClassNotFoundException, ParseException{
        
        String dateStr = "06/27/2007";
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = (Date)formatter.parse(dateStr); 
        
        MobileAccount acc=new MobileAccount("M123456",100,startDate,startDate,"one day pass",546,456,786,45);
        //Account ac=new Account("456789");
        //AC.createTempAccount(acc);
        //AC.add(ac);
        AccountControl.createMobileAccount(acc);
        AccountControl.serialize();
        mobileAccounts=AccountControl.deserialize();
        //Serialization.serialize(MC, "acc.ser");
        
        //MC= (Vector<MobileAccount>) Serialization.deserialize("acc.ser");
        //MobileAccount objs = AC.get(0).getAcNum();
        //System.out.print(acc.getAcNum());
        //System.out.print(acc.getNum());
        
         //System.out.print(AC.get(0).getNum() + " ");
         String cr;
         int sub;
        for (MobileAccount item:mobileAccounts) {
            
            cr=item.getAccountNumber();
             System.out.print(cr);
             sub=item.getSubscriberId();
             System.out.print(sub);
         }
            
         
        
        
        
        
        
  /*
     try {
   FileOutputStream fileOutputStream = new FileOutputStream(
     "serialObject.ser");
   ObjectOutputStream objectOutputStream = new ObjectOutputStream(
     fileOutputStream);
   objectOutputStream.writeObject(acc);

  } catch (FileNotFoundException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  } catch (IOException ioe) {
   // TODO Auto-generated catch block
   ioe.printStackTrace();
  }

  /*
   * Deserializing instance
   */
        /*
  MobileAccount Acc = null;

  try {
   FileInputStream fileInputStream = new FileInputStream(
     "serialobject.ser");
   ObjectInputStream inputStream = new ObjectInputStream(
     fileInputStream);
   Acc = (MobileAccount) inputStream.readObject();

  } catch (FileNotFoundException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  } catch (IOException ioe) {
   // TODO Auto-generated catch block
   ioe.printStackTrace();
  } catch (ClassNotFoundException cnf) {
   // TODO Auto-generated catch block
   cnf.printStackTrace();
  }

  /*
   * Printing values from deserialized object
   */
  //System.out.println("Printing value of Deserailized instance :");
 // System.out.println("Gender: " + Acc.getNum());
 

 }
    }

