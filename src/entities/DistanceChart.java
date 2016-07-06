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
    private double distance[][]=new double[100][100];
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

 
    
    
}
