/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.sql.Time;

/**
 *
 * @author Ravi
 */
public class TempLog extends Log implements Serializable{
    
    private int hops;

    public TempLog(Time timecreated, String route, String accountNumber, String checkInLocation, Time checkInTime, String checkOutLocation, Time checkOutTime, float fareCharged) {
        super(timecreated, route, accountNumber, checkInLocation, checkInTime, checkOutLocation, checkOutTime, fareCharged);
        
        this.hops=hops;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }
    
    
    
    
    
    
    
}
