/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *0 
 * @author HP
 */
public class Serialization {

public static void serialize(Object obj,String filename) throws IOException  
{
    
        FileOutputStream fileOut =new FileOutputStream(filename);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(obj);
        out.close();
        fileOut.close();
        out.flush();
        fileOut.flush();
    


}   

 
 
 
 public static Object deserialize(String filename) throws IOException, ClassNotFoundException {    
 
       FileInputStream fileIn = new FileInputStream(filename);
       ObjectInputStream in = new ObjectInputStream(fileIn);
       Object obj= in.readObject();
       in.close();
       fileIn.close();
       return obj;
  } 



    
}




