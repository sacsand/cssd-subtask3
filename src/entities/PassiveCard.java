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
public class PassiveCard extends TravelCard implements Serializable{
    
    private String area;
    private boolean direction;
    private String destination;

    public PassiveCard(Date expireDate, String pinNumber, String packageType, String acountNumber,String area,boolean direction,String destination) {
        super(expireDate, pinNumber, packageType, acountNumber);
        
        this.area=area;
        this.direction=direction;
        this.destination=destination;
    }

    public String getArea() {
        return area;
    }

    public boolean isDirection() {
        return direction;
    }

    public String getDestination() {
        return destination;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    
    
}
