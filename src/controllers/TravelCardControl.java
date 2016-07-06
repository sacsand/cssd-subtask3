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
public class TravelCardControl implements Serializable {
    
    private volatile static TravelCardControl uniqueInstance;
    public static synchronized TravelCardControl getInstance()
  {
      if(uniqueInstance==null)
      {
          synchronized(TravelCardControl.class)
          {
              if(uniqueInstance==null)
                uniqueInstance=new TravelCardControl();
          }
      }
      return uniqueInstance;
  }
    private TravelCardControl(){
        
    }
    private static Vector<PassiveCard> setOfPassiveCards= new Vector<PassiveCard>();
    private static Vector<SmartCard> setOfSmartCards= new Vector<SmartCard>();
    
    public void addSmartCard(SmartCard smartcard){
        setOfSmartCards.add(smartcard);
        
    }
    public void addPassiveCard(PassiveCard pasivecard){
        setOfPassiveCards.add(pasivecard);
    }
    public SmartCard findSmartCardByCardNo(String cardNo){
        for(int i=0;i<setOfSmartCards.size();i++)
            {
               for(SmartCard SC: setOfSmartCards )
                if((SC.getAcountNumber()).equals(cardNo))
                {
                    return SC;
                }else
                {
                    return null;
                }
            }
        return null;
    }
    public PassiveCard findPassiveCarByCardNo(String cardNo){
        for(int i=0;i<setOfPassiveCards.size();i++)
            {
               for(PassiveCard PC: setOfPassiveCards )
                if((PC.getAcountNumber()).equals(cardNo))
                {
                    return PC;
                }else
                {
                    return null;
                }
            }
        return null;
    }
    public static void serialize() throws IOException {
        Serialization.serialize(setOfPassiveCards, "passiveCards.ser");
        Serialization.serialize(setOfSmartCards, "smartCards.ser");
    }

    public static void deserialize() throws IOException, ClassNotFoundException {
        setOfPassiveCards = (Vector<PassiveCard>) Serialization.deserialize("passiveCards.ser");
        setOfSmartCards = (Vector<SmartCard>) Serialization.deserialize("smartCards.ser");
    }
}
