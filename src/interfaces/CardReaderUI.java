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
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anuradha
 */
public class CardReaderUI extends javax.swing.JFrame implements ActionListener {

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
    
    private static String ID;
    private String mobileAccountNum;
    private String AccNo;
    private Double fare;
    private String type;
    
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
            smartAc=theAccount.findSmartAccountByAccountNumber(smartcard.getAcountNumber());
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
        if(type.equals("SC")){
        amount=smartAc.getAmount();
        return  amount;
        }
        else if(type.equals("MA")){
        amount=mobileAc.getAmount();
        return  amount;
        }
        return 0;
    }
    public Double calculateFare(Double distance, Double charge){
        return distance*charge;
    }
    public void chargeAmount(Double fare){
        if(type.equals("SC")){
        smartAc.deductAmount(fare);
        }
        if(type.equals("MA")){
        mobileAc.deductAmount(fare);
        }
    }
    public void detectCard(){
        ID=jTextField_ID.getText();
        if(!ID.equals(""))
        {
            if(ID.contains("SC")){
                if(theTravelCard.findSmartCardByCardNo(ID)==null){
                    jLabel_message.setText("Error....! Please re-swipe the card again");
                }
                else{
                    smartcard=theTravelCard.findSmartCardByCardNo(ID);
                    type="SC";
                }
            }
            else if(ID.contains("PC")){
                if(theTravelCard.findPassiveCarByCardNo(ID)==null){
                    jLabel_message.setText("Error....! Please re-swipe the card again");
                }
                else{
                   passivecard=theTravelCard.findPassiveCarByCardNo(ID);
                    type="PC";
                }
            }
             else if(ID.contains("MA")){
                if(theAccount.findMobileAccountByAccountNumber(ID)==null){
                    jLabel_message.setText("Error....! Please tap the phone again");
                }
                else{
                   mobileAc=theAccount.findMobileAccountByAccountNumber(ID);
                   type="MA";
                }
            }
        }
        else{
            jLabel_message.setText("Please Swipe The card or tap the phone ");
            type="";
        }
        
    }
    public void showChargedAmount(){
        jLabel_message.setText("Charged amount Rs."+fare);
    }
    public void updateLogbyAccountNumber(String accountNumber){
        if(type.equals("SC")){
         CRLog=theLog.findCRLogByAccountNumber(accountNumber);
         CRLog.updateLog(currentLocation, currentTime,fare);
        }
        if(type.equals("MA")){
          MLog=theLog.findMobileLogByAccountNumber(accountNumber);
          MLog.setCheckOutLocation(currentLocation);
          MLog.setCheckOutTime(currentTime);
          MLog.setFareCharged(fare);
        }
    }
    public void feedback(){
        jLabel_message.setText("Location:"+currentLocation+", Time:"+currentTime);
    }
    public void test(){

        double distance;
        Double charge;
        double ammountNeeded;
        double currentAmount;
        
        currentTime=new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        currentLocation=jComboBox_locations.getSelectedItem().toString();
        detectCard();
        if(type.equals("SC")){
            if(smartcard!=null){
                connectCard();
                CRLog=theLog.findCRLogByAccountNumber(smartAc.getAccountNumber());
                System.out.println(theLog.findCRLogByAccountNumber(smartAc.getAccountNumber()));
                if(CRLog==null){
                    currentAmount=checkCredit();
                    System.out.println("amount"+currentAmount);
                    ammountNeeded=thePackage.findPackageByPackageID(smartAc.getPackageType()).getAmountNeeded();
                    if(currentAmount>ammountNeeded)
                    {
                     CardReaderLog newCRLog= new CardReaderLog(currentTime,route,AccNo,currentLocation,currentTime);
                     theLog.createCardReaderLog(newCRLog);
                        System.out.println(newCRLog);
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
                    fare=calculateFare(distance, charge);
                    chargeAmount(fare);
                    showChargedAmount();
                    updateLogbyAccountNumber(AccNo);
                    disconnect();
                    theLog.moveToMainLog(CRLog);
                }
            }
        }
        if(type.equals("PC")){
            AccNo=passivecard.getAcountNumber();
            TPLog=theLog.findTempLogByAccountNumber(AccNo);
            if(TPLog!=null){
                TempLog newTempLog= new TempLog(currentTime, route, AccNo, checkingLocation, currentTime, 1 );
            }
            else{
                
            }
        }
        if(type.equals("MA")){
            AccNo=mobileAc.getAccountNumber();
            MLog=theLog.findMobileLogByAccountNumber(AccNo);
            if(MLog==null){
                currentAmount=checkCredit();
                ammountNeeded=thePackage.findPackageByPackageID(mobileAc.getPackageType()).getAmountNeeded();
                if(currentAmount>ammountNeeded){
                    Log newMobileLog=new Log(currentTime, route, AccNo, checkingLocation, currentTime);
                    theLog.createMobileLog(MLog);
                }
                else{
                    jLabel_message.setText("insufficient Amount,Please recharge your account");
                }
                
            }
            else{
                checkingLocation=MLog.getCheckInLocation();
                distance=Dchart.getDistance(checkingLocation, currentLocation);
                charge=thePackage.getChargesByTime(thePackage.findPackageByPackageID(mobileAc.getPackageType()),currentTime);
                fare=calculateFare(distance,charge);
                showChargedAmount();
                updateLogbyAccountNumber(AccNo);
                theLog.moveToMainLog(MLog);
            }
        }
        try {
            theLog.serialize();
            theAccount.serialize();
        } catch (IOException ex) {
            Logger.getLogger(CardReaderUI.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
  public void test1(){
        SmartCard SmartCard = theTravelCard.findSmartCardByCardNo("SC0");
      System.out.println(SmartCard.getPackageType());
  }
    
    /**
     * Creates new form CardReaderUI
     */
    public CardReaderUI() {
        initComponents();
        setLocationRelativeTo(null);
        
        try {
            theAccount.deserialize();
            theLog.deserialize();
            thePackage.deserialize();
            theTravelCard.deserialize();
        } catch (IOException ex) {
            Logger.getLogger(CardReaderUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CardReaderUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        jButton_proceed.addActionListener(this);
        
        
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
        jTextField_ID = new javax.swing.JTextField();
        jButton_proceed = new javax.swing.JButton();
        jButton_endOfTrip = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setText("Current Location:");

        jLabel_message.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Card/NFC Reader"));

        jLabel2.setText("CardID/Mobile Account number:");

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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(219, 219, 219)
                        .addComponent(jButton_proceed, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_ID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jButton_proceed, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jButton_endOfTrip.setText("End of trip");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(46, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton_endOfTrip)
                        .addGap(18, 18, 18))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_locations, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(jLabel_message, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton_endOfTrip)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox_locations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_message, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_proceedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_proceedActionPerformed
        test();
    }//GEN-LAST:event_jButton_proceedActionPerformed

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
            java.util.logging.Logger.getLogger(CardReaderUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CardReaderUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CardReaderUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CardReaderUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 new CardReaderUI().setVisible(true);
               
                
           }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_endOfTrip;
    private javax.swing.JButton jButton_proceed;
    private javax.swing.JComboBox jComboBox_locations;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private static javax.swing.JLabel jLabel_message;
    private javax.swing.JPanel jPanel2;
    private static javax.swing.JTextField jTextField_ID;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
   if(e.getSource()== jButton_proceed){
   
       System.out.println("test action");
   
   }
   
    }
}
