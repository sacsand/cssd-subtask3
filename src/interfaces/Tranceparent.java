package interfaces;


import com.sun.awt.AWTUtilities;
import javax.swing.JFrame;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sachintha
 */
class Tranceparent {

   
    void TransCompFrame(LoadingScreen aThis) {
        aThis.setUndecorated(true);
        AWTUtilities.setWindowOpaque(aThis,false) ;
    }
    
}
