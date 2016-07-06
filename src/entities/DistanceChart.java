/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

/**
 *
 * @author Ravi
 */
public class DistanceChart {
    
    private double distance[][]=new double[100][100];
    
    public double getDistance(int source, int destination)
    {   double result=0;
        return result;
        
    }

    public void setDistance(int source, int destination, double value) {
        distance[source][destination]=value;
        
    }

 
    
    
}