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
    
    private volatile static DistanceChart uniqueInstance;
    public static synchronized DistanceChart getInstance()
  {
      if(uniqueInstance==null)
      {
          synchronized(DistanceChart.class)
          {
              if(uniqueInstance==null)
                uniqueInstance=new DistanceChart();
          }
      }
      return uniqueInstance;
  }
    private DistanceChart(){
        
    }
    private double distance[][]={{0,1,5,3,6,4,7,8,9,5,2,4,6,3},
                                 {1,0,6,8,6,4,2,5,9,1,3,5,4,6},
                                 {5,6,0,6,2,4,6,9,2,1,3,5,4,6},
                                 {3,8,6,0,4,5,2,1,7,6,8,9,2,5},
                                 {6,6,2,4,0,7,6,2,5,4,8,9,6,3},
                                 {4,4,4,5,7,0,7,8,4,5,6,2,1,5},
                                 {7,2,6,2,6,7,0,4,6,8,7,1,2,3},
                                 {8,5,9,1,2,8,4,0,4,6,2,1,5,8},
                                 {9,9,2,7,5,4,6,4,0,8,2,1,3,3},
                                 {5,1,1,6,4,5,8,6,8,0,4,5,5,4},
                                 {2,3,3,8,8,6,7,2,2,4,0,6,5,9},
                                 {4,5,5,9,9,2,1,1,1,5,6,0,5,7},
                                 {6,4,4,2,6,1,2,5,3,5,5,5,0,1},
                                 {3,6,6,5,3,5,3,8,3,4,9,7,1,0}};
                              
    private static String locations[]=new String[]{"Panadura","Walana","Old Galle Road","Keselwatta","Moratuwa","Ratmalana","Mt.Lavinia","Dehiwala","Wellawatta","Bambalapitiya","Kollupitiya","Galle Face","Fort","Pettah"};
    
    public double getDistance(String source,String destination)
    {   
        int sourceIndex=0;
        int destinationIndex=0;
        double travelDistance;
        for(int i=0;i<locations.length;i++)
        {
            if(locations[i].equals(source))
            {
                sourceIndex=i;
                break;
            }
        }
        for(int i=0;i<locations.length;i++)
        {
            if(locations[i].equals(destination))
            {
                destinationIndex=i;
                break;
            }
        }
        travelDistance=distance[sourceIndex][destinationIndex];
        return travelDistance;
    }

    public void setDistance(int source, int destination, double value) {
        distance[source][destination]=value;
        
    }
    public static String[] getLocations(){
        return locations;  
    }
    public boolean getDirection(String source,String destination){
        boolean direction;
        int destinationIndex=0;
        int sourceIndex=0;
        for(int i=0;i<locations.length;i++)
        {
            if(locations[i].equals(destination))
            {
                destinationIndex=i;
                break;
            }
        }
        for(int i=0;i<locations.length;i++)
        {
            if(locations[i].equals(destination))
            {
                sourceIndex=i;
                break;
            }
        }
        if(sourceIndex<destinationIndex)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

 
    //cheetah
    public String[] getAllLocations(){
        return locations;
    }
    
    
}
