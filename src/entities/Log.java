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
public class Log implements Serializable{

    public Log(Time timecreated, String route, String accountNumber, String checkInLocation, Time checkInTime, String checkOutLocation, Time checkOutTime, float fareCharged) {
        this.timecreated = timecreated;
        this.route = route;
        this.accountNumber = accountNumber;
        this.checkInLocation = checkInLocation;
        this.checkInTime = checkInTime;
        this.checkOutLocation = checkOutLocation;
        this.checkOutTime = checkOutTime;
        this.fareCharged = fareCharged;
    }
    
    private Time timecreated; 
    private String route;
    private String accountNumber;
    private String checkInLocation;
    private Time checkInTime;
    private String checkOutLocation;
    private Time checkOutTime;
    private float fareCharged;

    public Time getTimecreated() {
        return timecreated;
    }

    public String getRoute() {
        return route;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCheckInLocation() {
        return checkInLocation;
    }

    public Time getCheckInTime() {
        return checkInTime;
    }

    public String getCheckOutLocation() {
        return checkOutLocation;
    }

    public Time getCheckOutTime() {
        return checkOutTime;
    }

    public float getFareCharged() {
        return fareCharged;
    }

    public void setTimecreated(Time timecreated) {
        this.timecreated = timecreated;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setCheckInLocation(String checkInLocation) {
        this.checkInLocation = checkInLocation;
    }

    public void setCheckInTime(Time checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setCheckOutLocation(String checkOutLocation) {
        this.checkOutLocation = checkOutLocation;
    }

    public void setCheckOutTime(Time checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setFareCharged(float fareCharged) {
        this.fareCharged = fareCharged;
    }
    
            
    
}
