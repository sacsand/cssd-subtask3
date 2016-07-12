/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

/**
 *
 * @author Anuradha
 */
public class ThreadClass implements Runnable {

    public static boolean keepRunning = true;
    @Override
    public void run() {
        System.out.println("Starting to thread.");
        while (keepRunning) {
            System.out.println("Running thread...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("Done looping.");
    }
    
}
