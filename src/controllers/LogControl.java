/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entities.CardReaderLog;
import entities.TempLog;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author Anuradha
 */
public class LogControl implements Serializable{
    
    private static Vector<TempLog> setOfTempLogs= new Vector<TempLog>();
    private static Vector<CardReaderLog> setOfCarReaderLogs= new Vector<CardReaderLog>();
    
    public void createTempLog(TempLog log){
        setOfTempLogs.add(log); 
    }
     public void createCardReaderLog(CardReaderLog log){
        setOfCarReaderLogs.add(log); 
    }
     
    
    
}
