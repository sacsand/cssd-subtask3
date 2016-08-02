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

    public Log(String timecreated, String route, String accountNumber, String checkInLocation, String checkInTime, String checkOutLocation, String checkOutTime, Double fareCharged) {
        this.timecreated = timecreated;
        this.route = route;
        this.accountNumber = accountNumber;
        this.checkInLocation = checkInLocation;
        this.checkInTime = checkInTime;
        this.checkOutLocation = checkOutLocation;
        this.checkOutTime = checkOutTime;
        this.fareCharged = fareCharged;
    }
    public Log(String timecreated, String route, String accountNumber, String checkInLocation, String checkInTime) {
        this.timecreated = timecreated;
        this.route = route;
        this.accountNumber = accountNumber;
        this.checkInLocation = checkInLocation;
        this.checkInTime = checkInTime;
    }
    
    private String timecreated; 
    private String route;
    private String accountNumber;
    private String checkInLocation;
    private String checkInTime;
    private String checkOutLocation;
    private String checkOutTime;
    private Double fareCharged;

    public String getTimecreated() {
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

    public String getCheckInTime() {
        return checkInTime;
    }

    public String getCheckOutLocation() {
        return checkOutLocation;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public Double getFareCharged() {
        return fareCharged;
    }

    public void setTimecreated(String timecreated) {
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

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setCheckOutLocation(String checkOutLocation) {
        this.checkOutLocation = checkOutLocation;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setFareCharged(Double fareCharged) {
        this.fareCharged = fareCharged;
    }
    
            
    
}
