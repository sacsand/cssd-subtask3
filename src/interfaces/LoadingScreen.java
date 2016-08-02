package interfaces;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Principal;
import javax.swing.JFrame;
import javax.swing.Timer;
import interfaces.Login;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sachintha
 */
public class LoadingScreen extends javax.swing.JFrame  {
 
    private Timer t=null;
    private int count =0;  
  

    /**
     * Creates new form NewJFrame
     */
    public LoadingScreen() {
        new Tranceparent().TransCompFrame(this);
        initComponents();
        
         t=new Timer(100,new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               count ++;
                jProgressBar1.setValue(count);
                if(jProgressBar1.getValue()<100){
                   jProgressBar1.setValue( jProgressBar1.getValue()+1);
                }
                
                if(jProgressBar1.getValue()==5) {
                  loading_text.setText("lording module");
                 }
                if(jProgressBar1.getValue()==15) {
                  loading_text.setText("loading controllers");
                 }
                if(jProgressBar1.getValue()==25) {
                  loading_text.setText("lording classes");
                 }
                if(jProgressBar1.getValue()==35) {
                  loading_text.setText("Try To Connect Main Server");
                 }
                if(jProgressBar1.getValue()==50) {
                  loading_text.setText("Conected");
                 }
                if(jProgressBar1.getValue()==56) {
                  loading_text.setText("Conecting to proxy server");
                 }
                if(jProgressBar1.getValue()==65) {
                  loading_text.setText("Fething Data");
                 }
                 if(jProgressBar1.getValue()==80) {
                  loading_text.setText("Fething Data");
                 }
                 if(jProgressBar1.getValue()==80) {
                  loading_text.setText("Loadin Views");
                 }
                
                if(jProgressBar1.getValue()==100) {
                     setVisible(false);
                     Login x3=new Login();
                     x3.setVisible(true);
                     t.stop();
                
                 
                }
                
                }      
                
                
            });
        setLocationRelativeTo(null) ;
            t.start();
       
   
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        Page_Title12 = new javax.swing.JLabel();
        Page_Title6 = new javax.swing.JLabel();
        loading_text = new javax.swing.JLabel();
        Page_Title7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocation(new java.awt.Point(450, 100));
        setMinimumSize(new java.awt.Dimension(325, 520));
        setPreferredSize(new java.awt.Dimension(325, 500));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(jProgressBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 290, 10));

        Page_Title12.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title12.setFont(new java.awt.Font("Ubuntu Light", 0, 14)); // NOI18N
        Page_Title12.setForeground(java.awt.Color.white);
        Page_Title12.setText("beta 2");
        getContentPane().add(Page_Title12, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, -1, -1));

        Page_Title6.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title6.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title6.setForeground(java.awt.Color.white);
        Page_Title6.setText("Lydia");
        getContentPane().add(Page_Title6, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, -1, 50));

        loading_text.setForeground(new java.awt.Color(255, 255, 255));
        loading_text.setText("jLabel1");
        getContentPane().add(loading_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 180, 20));

        Page_Title7.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title7.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title7.setForeground(java.awt.Color.white);
        Page_Title7.setText("Welcome to Lydia Travel Services");
        getContentPane().add(Page_Title7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-30, -20, 350, 580));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoadingScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoadingScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoadingScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoadingScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoadingScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Page_Title12;
    private javax.swing.JLabel Page_Title6;
    private javax.swing.JLabel Page_Title7;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel loading_text;
    // End of variables declaration//GEN-END:variables

   
  
        
}