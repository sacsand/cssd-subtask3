/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Ravi
 */
public class SmartCard extends TravelCard implements Serializable{
    
    private boolean authentication;

    public SmartCard(Date expireDate, String pinNumber, String packageType, String acountNumber,boolean authentication) {
        super(expireDate, pinNumber, packageType, acountNumber);
        this.authentication= authentication;
    }

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }
    
    public String validate(String MD5key){
        return null;
    }
    public String encrypt(String data){
        return null;
    }
    
    public void disconnect()
    {
        
    }
   
    
    
    
}
