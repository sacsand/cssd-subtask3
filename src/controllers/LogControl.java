/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.*;
import entities.TempLog;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Anuradha
 */
public class LogControl implements Serializable{
    
    private volatile static LogControl uniqueInstance;
    public static synchronized LogControl getInstance()
  {
      if(uniqueInstance==null)
      {
          synchronized(LogControl.class)
          {
              if(uniqueInstance==null)
                uniqueInstance=new LogControl();
          }
      }
      return uniqueInstance;
  }
    private LogControl(){
        
    }
    private static Vector<TempLog> setOfTempLogs= new Vector<TempLog>();
    private static Vector<CardReaderLog> setOfCarReaderLogs= new Vector<CardReaderLog>();
    private static Vector<Log>  setOfMobileLogs= new Vector<Log>();
    private static Vector<Log> MainLog= new Vector<Log>();
    
    public void createTempLog(TempLog log){
        setOfTempLogs.add(log); 
    }
     public void createCardReaderLog(CardReaderLog log){
        setOfCarReaderLogs.add(log); 
    }
     public void createMobileLog(Log log){
        setOfMobileLogs.add(log); 
    }
     public void moveToMainLog(Log log){
         MainLog.add(log);
         setOfMobileLogs.remove(log);
     }
     public void moveToMainLog(CardReaderLog CRlog){
         MainLog.add(CRlog);
         setOfCarReaderLogs.remove(CRlog);
     }
     public void moveToMainLog(TempLog TPlog){
         MainLog.add(TPlog);
         setOfTempLogs.remove(TPlog);
     }
     public CardReaderLog findCRLogByAccountNumber(String accountNumber){
         
            for(CardReaderLog CR: setOfCarReaderLogs )
            {
                if((CR.getAccountNumber()).equals(accountNumber))
                {
                    return CR;
                }else
                {
                    return null;
                }
            }
        return null;
     }
      public TempLog findTempLogByAccountNumber(String accountNumber){
         TempLog tempLog=null;
               for(TempLog TL: setOfTempLogs )
               {
                    if((TL.getAccountNumber()).equals(accountNumber))
                    {
                        tempLog=TL;
                        return tempLog;
                    }else
                    {
                        return tempLog;
                    }
               }
        return null;
     }
       public Log findMobileLogByAccountNumber(String accountNumber){
         Log mobileLog=null;
               for(Log ML: setOfMobileLogs )
               {
                if((ML.getAccountNumber()).equals(accountNumber))
                {
                    mobileLog=ML;
                    return mobileLog;
                }else
                {
                    return mobileLog;
                }
               }
        return null;
     }
     public Log findLogByTime(Time time){
         return null;
     }
     public Log findLogByDate(Date date){
         return null;
     }
     public Log findLogByRoute(String route){
         return null;
     }
     public Vector<Log> getLogsByAccount(String accountNumber){
         Vector<Log> logs = null;
         for(Log lg:MainLog)
         {
                if((lg.getAccountNumber()).equals(accountNumber))
                {
                    logs.add(lg);
                }
                
         }
        return logs;
     }
    
    public static void serialize() throws IOException{
      Serialization.serialize(setOfTempLogs, "tempLogs.ser");
      Serialization.serialize(setOfCarReaderLogs, "carReaderLogs.ser");
      Serialization.serialize(setOfMobileLogs, "mobileLogs.ser");
      Serialization.serialize(MainLog, "mainLogs.ser");
    }
    public static void deserialize() throws IOException, ClassNotFoundException{
       setOfTempLogs=(Vector<TempLog>) Serialization.deserialize("tempLogs.ser");
       setOfCarReaderLogs=(Vector<CardReaderLog>) Serialization.deserialize("carReaderLogs.ser");
       setOfMobileLogs=(Vector<Log>) Serialization.deserialize("mobileLogs.ser");
       MainLog=(Vector<Log>) Serialization.deserialize("mainLogs.ser");
    }
}
