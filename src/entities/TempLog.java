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

    public TempLog(String timecreated, String route, String accountNumber, String checkInLocation, String checkInTime, String checkOutLocation, String checkOutTime, float fareCharged) {
        super(timecreated, route, accountNumber, checkInLocation, checkInTime, checkOutLocation, checkOutTime, fareCharged);
        
        this.hops=hops;
    }
    public TempLog(String timecreated, String route, String accountNumber, String checkInLocation, String checkInTime, int hops) {
        super(timecreated, route, accountNumber, checkInLocation, checkInTime);
        
        this.hops=hops;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }
    
    
    
    
    
    
    
}
