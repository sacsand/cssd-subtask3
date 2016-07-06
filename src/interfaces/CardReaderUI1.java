/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import controllers.*;
import entities.*;
import entities.SmartCard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 *
 * @author Anuradha
 */
public class CardReaderUI1 extends javax.swing.JFrame implements ActionListener {

    private String cardReaderID;
    private String currentLocation;
    private String currentTime;
    private String route;
    private String checkingLocation;
    
    private AccountControl theAccount=AccountControl.getInstance();
    private PackageControl thePackage=PackageControl.getInstance();
    private LogControl theLog=LogControl.getInstance();
    private TravelCardControl theTravelCard=TravelCardControl.getInstance();
    
    private SmartCard smartcard; 
    private PassiveCard passivecard;
    private SmartAccount smartAc;
    private MobileAccount mobileAc;
    private TempAccount tempAc;
    private LogControl logcontrol;
    private CardReaderLog CRLog;
    private TempLog TPLog;
    private Log MLog;
    private DistanceChart Dchart=DistanceChart.getInstance();
    
    private static String smartcardNum;
    private String mobileAccountNum;
    private String AccNo;
    private float fare;
    
//    volatile boolean keepRunning = true;
//    
//    public static void run(){
//     
//        ThreadClass reader = new ThreadClass();
//        Thread t = new Thread(reader);
//        t.start();
//        detectCard();
//        while(smartcardNum.isEmpty())
//        {
//           detectCard();
//        }
//        ThreadClass.keepRunning = false;
//        t.interrupt(); 
//    }
    public void connectCard(){
        String key;
        String newkey="MD5 KEY";
        key=smartcard.authenticate();
        if(smartcard.validate(newkey))
        {
            smartcard.setAuthentication(true);
            AccNo=smartcard.getAcountNumber();
        }
        else{
            jLabel_message.setText("Authentication Error....");
        }
    }
    public void disconnect(){
        smartcard.setAuthentication(false);
        smartcard=null;
    }
    public void updateLocation(String tempLocation){
        
    }
    public double checkCredit(){
        double amount;
        amount=theAccount.findSmartAccountByAccountNumber(AccNo).getAmount();
        return  amount;
    }
    public float calculateFare(float distance, float charge){
        return distance*charge;
    }
    public void chargeAmount(float fare){
        theAccount.findSmartAccountByAccountNumber(AccNo).deductAmount(fare);
    }
    public void detectCard(){
        smartcardNum=jTextField_samrtCardNum.getText();
        if(!smartcardNum.equals(""))
        {
        if(theTravelCard.findSmartCardByCardNo(smartcardNum)==null){
            jLabel_message.setText("Error....! Please re-insert the card again");
        }
            smartcard=theTravelCard.findSmartCardByCardNo(smartcardNum);
        }
        else{
            jLabel_message.setText("Please Insert The card ");
        }
        
    }
    public void showChargedAmount(){
        jLabel_message.setText("Charged amount Rs."+fare);
    }
    public void updateLogbyAccountNumber(String accountNumber){
        CRLog=theLog.findCRLogByAccountNumber(accountNumber);
        CRLog.updateLog(currentLocation, currentTime,fare);
    }
    public void feedback(){
        jLabel_message.setText("Check in Location:"+currentLocation+", Time:"+currentTime);
    }
    public void test(){

        double distance;
        float charge;
        float ammountNeeded;
        float currentAmount;
        
        currentTime=new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        currentLocation=jComboBox_locations.getSelectedItem().toString();
        detectCard();
        if(smartcard!=null){
            connectCard();
            CRLog=theLog.findCRLogByAccountNumber(AccNo);
            if(CRLog==null){
                currentAmount=(float) checkCredit();
                ammountNeeded=thePackage.findPackageByPackageID(smartcard.getPackageType()).getAmountNeeded();
                if(currentAmount>ammountNeeded)
                {
                 CardReaderLog newCRLog= new CardReaderLog(currentTime,route,AccNo,currentLocation,currentTime);
                 theLog.createCardReaderLog(newCRLog);
                }
                else
                {
                    jLabel_message.setText("insufficient Amount,Please recharge your account");
                }
            }
            else{
                checkingLocation=CRLog.getCheckInLocation();
                distance=Dchart.getDistance(checkingLocation, currentLocation);
                charge=thePackage.findPackageByPackageID(smartcard.getPackageType()).getPeakCharge();
                fare=calculateFare((float) distance, charge);
                chargeAmount(fare);
                showChargedAmount();
                updateLogbyAccountNumber(AccNo);
                disconnect();
            }
        }
    }
    public void mobile(){
        currentTime=new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        currentLocation=jComboBox_locations.getSelectedItem().toString();
        mobileAccountNum=jTextField_mobileAccountNum.getText();
        if(!mobileAccountNum.isEmpty()){
            mobileAc=theAccount.findMobileAccountByAccountNumber(mobileAccountNum);
        }
        else{
            jLabel_message.setText("Please Tap the phone to the device ");
        }
    }
    
    /**
     * Creates new form CardReaderUI
     */
    public CardReaderUI1() {
        initComponents();
        setLocationRelativeTo(null);
        
        jButton_proceed.addActionListener(this);
        jButton_proceedMobile.addActionListener(this);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        String locations[]=DistanceChart.getLocations();
        jComboBox_locations = new javax.swing.JComboBox(locations);
        jLabel_message = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_samrtCardNum = new javax.swing.JTextField();
        jButton_proceed = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel_accountNumber = new javax.swing.JLabel();
        jTextField_mobileAccountNum = new javax.swing.JTextField();
        jButton_proceedMobile = new javax.swing.JButton();
        jButton_endOfTrip = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Current Location:");

        jLabel_message.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Card Reader"));

        jLabel2.setText("Card Number:");

        jButton_proceed.setText("Proceed");
        jButton_proceed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_proceedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextField_samrtCardNum, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_proceed, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(94, 94, 94))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_samrtCardNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton_proceed, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("NFC"));

        jLabel_accountNumber.setText("Account Number:");

        jButton_proceedMobile.setText("Proceed");
        jButton_proceedMobile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_proceedMobileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_accountNumber)
                .addGap(18, 18, 18)
                .addComponent(jTextField_mobileAccountNum)
                .addGap(2, 2, 2))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(108, Short.MAX_VALUE)
                .addComponent(jButton_proceedMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_accountNumber)
                    .addComponent(jTextField_mobileAccountNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton_proceedMobile, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        jButton_endOfTrip.setText("End of trip");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addComponent(jLabel1)
                .addGap(44, 44, 44)
                .addComponent(jComboBox_locations, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_message, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jButton_endOfTrip)
                .addGap(34, 34, 34))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox_locations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel_message, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_endOfTrip)
                        .addGap(22, 22, 22))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_proceedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_proceedActionPerformed
        test();
    }//GEN-LAST:event_jButton_proceedActionPerformed

    private void jButton_proceedMobileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_proceedMobileActionPerformed
        mobile();
    }//GEN-LAST:event_jButton_proceedMobileActionPerformed

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
            java.util.logging.Logger.getLogger(CardReaderUI1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CardReaderUI1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CardReaderUI1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CardReaderUI1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CardReaderUI1().setVisible(true);
                
           }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_endOfTrip;
    private javax.swing.JButton jButton_proceed;
    private javax.swing.JButton jButton_proceedMobile;
    private javax.swing.JComboBox jComboBox_locations;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel_accountNumber;
    private static javax.swing.JLabel jLabel_message;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField_mobileAccountNum;
    private static javax.swing.JTextField jTextField_samrtCardNum;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
   if(e.getSource()== jButton_proceed){
   
       System.out.println("test action");
   
   }
   else if(e.getSource()== jButton_proceedMobile){
   
       System.out.println("test");
   
   }
    }
}
