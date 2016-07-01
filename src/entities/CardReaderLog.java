/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.sql.Time;

/**
 *
 * @author Ravi
 */
public class CardReaderLog extends Log{

    public CardReaderLog(Time timecreated, String route, String accountNumber, String checkInLocation, Time checkInTime, String checkOutLocation, Time checkOutTime, float fareCharged) {
        super(timecreated, route, accountNumber, checkInLocation, checkInTime, checkOutLocation, checkOutTime, fareCharged);
    }
    
    public void updateLog(String destination,Time time){
        
    }
    
}
