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

    public CardReaderLog(String timecreated, String route, String accountNumber, String checkInLocation, String checkInTime, String checkOutLocation, String checkOutTime, float fareCharged) {
        super(timecreated, route, accountNumber, checkInLocation, checkInTime, checkOutLocation, checkOutTime, fareCharged);
    }
    public CardReaderLog(String timecreated, String route, String accountNumber, String checkInLocation, String checkInTime) {
        super(timecreated, route, accountNumber, checkInLocation, checkInTime);
    }
    
    public void updateLog(String destination,String time,float fare){
        this.setCheckOutLocation(destination);
        this.setCheckOutTime(time);
        this.setFareCharged(fare);
    }
    
}
