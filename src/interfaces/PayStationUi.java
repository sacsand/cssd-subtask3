/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import controllers.AccountControl;
import controllers.TravelCardControl;
import entities.DistanceChart;
import entities.PassiveCard;
import entities.SmartAccount;
import entities.SmartCard;
import entities.TempAccount;
import entities.TravelCard;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author sachintha
 */
public class PayStationUi extends javax.swing.JFrame {

    private static SmartAccount currentSmartAcc = null;
    private static String currentPackage = null;
    private static SmartCard currentSmartCard = null;
    private static boolean currentPassiveCard = false;
    private static double passiveChargeAmount=0;
    private static AccountControl theAccounts=AccountControl.getInstance();
    private static TravelCardControl theTravelCards= TravelCardControl.getInstance();
    
    
    File smartAccountFile = new File("smartAccounts.ser");
    File smartCardFile = new File("smartCards.ser");

    /**
     * Creates new form atm
     */
    public PayStationUi() {
        initComponents();

        paystaionCardIdInvalid.setVisible(false); //hide invalid card id error lable
        paystaionCardPinNoMatch.setVisible(false);//hide pin no match error lable
        paystationInvalidAmount.setVisible(false);//hide invalid amount error lable
        passiveCardNormalNext.setVisible(false);//hide passive card normal next
    }

    public void issueCard(int pin) throws ParseException, IOException, ClassNotFoundException {

        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        String dateString = formatter.format(new Date());
        Date startDate = formatter.parse(dateString);

        if (currentPassiveCard == false) //if it's a smart card
        {

            if (smartAccountFile.exists()) {
               theAccounts.deserialize();

            }
            if (smartCardFile.exists()) {

                theTravelCards.deserialize();
            }

            //create a smart account
            SmartAccount smartAcc = new SmartAccount(startDate, startDate);
            theAccounts.createSmartAccount(smartAcc);
            System.out.println("smartACC no " + smartAcc.getAccountNumber());

            //create a smartcard
            SmartCard smartCard = new SmartCard(startDate, pin, smartAcc.getAccountNumber(), false);
            theTravelCards.addSmartCard(smartCard);
            System.out.println("smartcrd ID " + smartCard.getCardId());

            //set smart acc's cardid from smartcard
            smartAcc.setCardId(smartCard.getCardId());
            System.out.println("smartACC ID " + smartAcc.getCardId());

            currentSmartAcc = smartAcc; //set current smart acount
            currentSmartCard = smartCard;//set current smart card

            // System.out.println("smartACC card id " + smartAcc.getCardId());
            // AccountControl.serialize();
        } else {

            try {
            // TODO add your handling code here:

                //setting a value to the distance chart
                // DistanceChart.getInstance().setDistance(0, 0, 10);
                theAccounts.deserialize();
                theTravelCards.deserialize();

                displayCharge();

                TempAccount tempAcc = new TempAccount(startDate, startDate);
                theAccounts.createTempeAccount(tempAcc);

                PassiveCard passivecard = new PassiveCard(false, "colombo", startDate, tempAcc.getAccountNumber());
                TravelCardControl.getInstance().addPassiveCard(passivecard);

                tempAcc.setCardId(passivecard.getCardId());

                theAccounts.serialize();
                theTravelCards.serialize();

//             AccountControl.deserialize();
//             TravelCardControl.getInstance().deserialize();
            } catch (IOException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void topUpAccount(double amount, String accNo) {

        System.out.println("top up method eke accNo parameter:" + accNo);
        currentSmartAcc = theAccounts.findSmartAccountByAccountNumber(accNo);
//       System.out.println("current smartAcc acc no to top up:" + currentSmartAcc.getAccountNumber());
        currentSmartAcc.topUp(amount);
        try {
            theAccounts.serialize();
            theTravelCards.serialize();
        } catch (IOException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void selectPackage(String currentPackage) {
        this.currentPackage = currentPackage;

    }

    public void validateSmartCardId(String cardID, int pin) {

        currentSmartAcc = null;
        currentPackage = null;

        if (smartCardFile.exists()) {
            try {
                theTravelCards.deserialize();
            } catch (IOException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        currentSmartCard = TravelCardControl.getInstance().findSmartCardByCardNo(cardID);

        if ((currentSmartCard != null) && (currentSmartCard.getPinNumber() == pin)) { //if the smart card id correct

            // removing pane
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(atm_payment_selct);
            atm_outer.repaint();
            atm_outer.revalidate();

            System.out.println("ddddd");
            System.out.println("in validation sc acc no: " + currentSmartCard.getAcountNumber());
        } else {
            paystaionCardIdInvalid.setVisible(true);

        }

    }

    public void acceptPayment(double amount) {

        if (currentPassiveCard == false) { //if it's smart card
            if (currentSmartAcc != null) { //new smart card
                currentSmartAcc.setPackageType(currentPackage);
                topUpAccount(amount, currentSmartAcc.getAccountNumber());

                System.out.println("current smart acc" + currentSmartAcc.getAmount());
                jLabel_paystationSmartAccNum1.setText(currentSmartAcc.getAccountNumber());
                jLabel_paystationSmartCard1.setText(currentSmartAcc.getCardId());
                jLabel_paystationSmartCardBalance1.setText(String.valueOf(currentSmartAcc.getAmount()));
                jLabel_paystationSmartCardPack1.setText(String.valueOf(currentSmartAcc.getPackageType()));

//                currentSmartAcc = null;
//                currentPackage = null;
//                currentSmartCard = null;

            } else if (currentSmartCard != null) { //top-up an exsisting smart card
                try {
                    theAccounts.deserialize();
                } catch (IOException ex) {
                    Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("current smart acc in accept button" + currentSmartCard.getAcountNumber());
                topUpAccount(amount, currentSmartCard.getAcountNumber());
                System.out.println("current smart acc" + currentSmartAcc.getAmount());

                currentSmartAcc = theAccounts.findSmartAccountByAccountNumber(currentSmartCard.getAcountNumber());
                jLabel_paystationSmartAccNum1.setText(currentSmartCard.getAcountNumber());
                jLabel_paystationSmartCard1.setText(currentSmartAcc.getCardId());
                jLabel_paystationSmartCardBalance1.setText(String.valueOf(currentSmartAcc.getAmount()));
                jLabel_paystationSmartCardPack1.setText(String.valueOf(currentSmartAcc.getPackageType()));

//                currentSmartAcc = null;
//                currentPackage = null;
//                currentSmartCard = null;

            }

            // removing pane
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(card_paid);
            atm_outer.repaint();
            atm_outer.revalidate();

        } else { //if it's passive card
           // currentPassiveCard = false;
            
            
            // removing pane
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(passiveCardPaid);
            atm_outer.repaint();
            atm_outer.revalidate();
            System.out.println("passive card payments");

        }
        
        
                currentSmartAcc = null;
                currentPackage = null;
                currentSmartCard = null;
                currentPassiveCard=false;

    }

    public void validatePin(int pin, int rePin) {

        if (pin == rePin) {
            System.out.println("eqauls");

            try {
                issueCard(pin);

                // TODO add your handling code here:
                // removing pane
                atm_outer.removeAll();
                atm_outer.repaint();
                atm_outer.revalidate();
                //adding pane
                atm_outer.add(select_package);
                atm_outer.repaint();
                atm_outer.revalidate();
            } catch (ParseException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("not eqauls");
            paystaionCardPinNoMatch.setVisible(true);
            paystaionCardPinNoMatch.setText("PIN numbers doesn't match");
        }
    }

    public double calculateCharge(double distance) {

        return distance * 10;
    }

    public void displayCharge() {

        double distanceValue = 10;//anuradha
        double rate = 10;//isuru

        passiveCardTotal.setText(String.valueOf(calculateCharge(distanceValue)));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        atm_outer = new javax.swing.JPanel();
        atm_home = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        Page_Title30 = new javax.swing.JLabel();
        Page_Title5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Page_Title7 = new javax.swing.JLabel();
        Page_Title6 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        atm_payment_selct = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel_paystaionPaymentHome = new javax.swing.JLabel();
        jLabel_paystaionPaymentBack = new javax.swing.JLabel();
        Page_Title8 = new javax.swing.JLabel();
        Page_Title9 = new javax.swing.JLabel();
        Page_Title10 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        Page_Title32 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel_payStationBack = new javax.swing.JLabel();
        card_selet = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel_paystaionCardSelectHome = new javax.swing.JLabel();
        jLabel_paystaionCardSelectBack = new javax.swing.JLabel();
        Page_Title11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        Page_Title12 = new javax.swing.JLabel();
        Page_Title14 = new javax.swing.JLabel();
        Page_Title13 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        Page_Title31 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        card_pay = new javax.swing.JPanel();
        Page_Title15 = new javax.swing.JLabel();
        jLabel_paystaionCardPayHome = new javax.swing.JLabel();
        jLabel_paystaionCardPayBack = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        cardPayAmount = new javax.swing.JTextField();
        Page_Title16 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        Page_Title19 = new javax.swing.JLabel();
        Page_Title17 = new javax.swing.JLabel();
        Page_Title33 = new javax.swing.JLabel();
        Page_Title18 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        card_paid = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        Page_Title20 = new javax.swing.JLabel();
        jLabel_paystationSmartAccNum = new javax.swing.JLabel();
        jLabel_paystationSmartAccNum1 = new javax.swing.JLabel();
        jLabel_paystationSmartCard = new javax.swing.JLabel();
        jLabel_paystationSmartCard1 = new javax.swing.JLabel();
        jLabel_paystationSmartCardPack = new javax.swing.JLabel();
        jLabel_paystationSmartCardPack1 = new javax.swing.JLabel();
        jLabel_paystationSmartCardBalance = new javax.swing.JLabel();
        jLabel_paystationSmartCardBalance1 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Page_Title22 = new javax.swing.JLabel();
        Page_Title23 = new javax.swing.JLabel();
        Page_Title24 = new javax.swing.JLabel();
        Page_Title25 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        cash_pay = new javax.swing.JPanel();
        Page_Title21 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel_paystaionCashPayHome = new javax.swing.JLabel();
        jLabel_paystaionCashPayBack = new javax.swing.JLabel();
        jTextField_paystationCashAmount = new javax.swing.JTextField();
        Page_Title26 = new javax.swing.JLabel();
        paystationInvalidAmount = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        Page_Title27 = new javax.swing.JLabel();
        Page_Title28 = new javax.swing.JLabel();
        Page_Title29 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        select_package = new javax.swing.JPanel();
        Page_Title39 = new javax.swing.JLabel();
        jLabel_paystaionPackageHome = new javax.swing.JLabel();
        jLabel_paystaionPackageBack = new javax.swing.JLabel();
        jLabel_paystationMegaPack = new javax.swing.JLabel();
        Page_Title34 = new javax.swing.JLabel();
        Page_Title36 = new javax.swing.JLabel();
        jLabel_paystationBudjectPack = new javax.swing.JLabel();
        jLabel_paystationNightPack = new javax.swing.JLabel();
        Page_Title35 = new javax.swing.JLabel();
        Page_Title37 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel_paystationDayPack = new javax.swing.JLabel();
        Page_Title38 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel_selectSmartCradOptions = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel_paystaionSmartCardOptionHome = new javax.swing.JLabel();
        jLabel_paystaionSmartCardOptionBack = new javax.swing.JLabel();
        Page_Title40 = new javax.swing.JLabel();
        jLable_smartCardTopUp = new javax.swing.JLabel();
        jLable_newSmartCard = new javax.swing.JLabel();
        Page_Title43 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        enterCardId = new javax.swing.JPanel();
        paystaionCardIdInvalid = new javax.swing.JLabel();
        paystaionCardId2 = new javax.swing.JLabel();
        jLabel_paystaionCardPayHome1 = new javax.swing.JLabel();
        jLabel_paystaionCardPayBack1 = new javax.swing.JLabel();
        paystaionCardIdInput = new javax.swing.JTextField();
        paystaionCardPinTopUp = new javax.swing.JLabel();
        paystaionCardPinTopUp1 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        paystaionCardIdEnter = new javax.swing.JLabel();
        paystaionCardIdEnterText = new javax.swing.JLabel();
        paystaionCardId = new javax.swing.JLabel();
        Page_Title47 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        enterCardPin = new javax.swing.JPanel();
        paystaionCardPinNoMatch = new javax.swing.JLabel();
        paystaionCardId3 = new javax.swing.JLabel();
        jLabel_paystaionCardPayHome2 = new javax.swing.JLabel();
        jLabel_paystaionCardPayBack2 = new javax.swing.JLabel();
        paystaionCardPinInput2 = new javax.swing.JTextField();
        paystaionCardPinInput1 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        paystaionCardPinSubmit = new javax.swing.JLabel();
        paystaionCardIdEnterText1 = new javax.swing.JLabel();
        paystaionCardId1 = new javax.swing.JLabel();
        paystaionCardId4 = new javax.swing.JLabel();
        Page_Title48 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        paystationPassiveCardNormal = new javax.swing.JPanel();
        passiveCardNormalNext = new javax.swing.JLabel();
        passivceCardNormalCheck = new javax.swing.JLabel();
        passiveCardTotal = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        passiveCardSourse = new javax.swing.JComboBox();
        passiveCardDestination = new javax.swing.JComboBox();
        jLabel_paystaionPaymentHome1 = new javax.swing.JLabel();
        jLabel_paystaionPaymentBack1 = new javax.swing.JLabel();
        Page_Title53 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        Page_Title44 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        Page_Title45 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel_payStationBack1 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        passiveCardPaid = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        Page_Title46 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        Page_Title50 = new javax.swing.JLabel();
        Page_Title51 = new javax.swing.JLabel();
        Page_Title52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        passivePackages = new javax.swing.JPanel();
        jLabel_paystaionPackageHome1 = new javax.swing.JLabel();
        jLabel_paystaionPackageBack1 = new javax.swing.JLabel();
        Page_Title54 = new javax.swing.JLabel();
        jLabel_paystationNightPack1 = new javax.swing.JLabel();
        Page_Title56 = new javax.swing.JLabel();
        Page_Title57 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel_paystationDayPack1 = new javax.swing.JLabel();
        Page_Title58 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(629, 390));
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(null);

        atm_outer.setLayout(new java.awt.CardLayout());

        atm_home.setLayout(null);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel2.setText("Add");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        atm_home.add(jLabel2);
        jLabel2.setBounds(90, 230, 460, 60);

        Page_Title30.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title30.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title30.setForeground(java.awt.Color.white);
        Page_Title30.setText("Select a option");
        atm_home.add(Page_Title30);
        Page_Title30.setBounds(50, 90, 170, 30);

        Page_Title5.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title5.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title5.setForeground(java.awt.Color.white);
        Page_Title5.setText("Get Tickets");
        atm_home.add(Page_Title5);
        Page_Title5.setBounds(270, 230, 170, 60);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel3.setText("Add");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        atm_home.add(jLabel3);
        jLabel3.setBounds(90, 150, 460, 60);

        Page_Title7.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title7.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title7.setForeground(java.awt.Color.white);
        Page_Title7.setText("Get Smart Card");
        atm_home.add(Page_Title7);
        Page_Title7.setBounds(250, 150, 170, 60);

        Page_Title6.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title6.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title6.setForeground(java.awt.Color.white);
        Page_Title6.setText("Lydia ATM ");
        atm_home.add(Page_Title6);
        Page_Title6.setBounds(20, 0, 260, 60);

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel26.setText("Add");
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel26MouseClicked(evt);
            }
        });
        atm_home.add(jLabel26);
        jLabel26.setBounds(0, 0, 630, 60);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        atm_home.add(jLabel1);
        jLabel1.setBounds(-10, -700, 695, 1290);

        atm_outer.add(atm_home, "card2");

        atm_payment_selct.setLayout(null);

        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/cardIcon-Smalcal-50.png"))); // NOI18N
        atm_payment_selct.add(jLabel58);
        jLabel58.setBounds(100, 130, 113, 70);

        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-Small-50.png"))); // NOI18N
        atm_payment_selct.add(jLabel57);
        jLabel57.setBounds(100, 200, 70, 90);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel4.setText("Add");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        atm_payment_selct.add(jLabel4);
        jLabel4.setBounds(90, 210, 460, 70);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel5.setText("Add");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        atm_payment_selct.add(jLabel5);
        jLabel5.setBounds(90, 130, 460, 70);

        jLabel_paystaionPaymentHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel_paystaionPaymentHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPaymentHomeMouseClicked(evt);
            }
        });
        atm_payment_selct.add(jLabel_paystaionPaymentHome);
        jLabel_paystaionPaymentHome.setBounds(580, 10, 40, 30);

        jLabel_paystaionPaymentBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionPaymentBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPaymentBackMouseClicked(evt);
            }
        });
        atm_payment_selct.add(jLabel_paystaionPaymentBack);
        jLabel_paystaionPaymentBack.setBounds(530, 10, 40, 30);

        Page_Title8.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title8.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title8.setForeground(java.awt.Color.white);
        Page_Title8.setText("Pay by Cash");
        atm_payment_selct.add(Page_Title8);
        Page_Title8.setBounds(270, 220, 170, 50);

        Page_Title9.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title9.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title9.setForeground(java.awt.Color.white);
        Page_Title9.setText("Pay by Card");
        atm_payment_selct.add(Page_Title9);
        Page_Title9.setBounds(270, 150, 170, 30);

        Page_Title10.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title10.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title10.setForeground(java.awt.Color.white);
        Page_Title10.setText("Lydia ATM ");
        atm_payment_selct.add(Page_Title10);
        Page_Title10.setBounds(20, 0, 260, 60);

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel25.setText("Add");
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        atm_payment_selct.add(jLabel25);
        jLabel25.setBounds(0, 0, 630, 60);

        Page_Title32.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title32.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title32.setForeground(java.awt.Color.white);
        Page_Title32.setText("Select a option");
        atm_payment_selct.add(Page_Title32);
        Page_Title32.setBounds(50, 90, 170, 30);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        atm_payment_selct.add(jLabel6);
        jLabel6.setBounds(-10, -700, 695, 1290);

        jLabel_payStationBack.setText("Back");
        atm_payment_selct.add(jLabel_payStationBack);
        jLabel_payStationBack.setBounds(50, 300, 80, 40);

        atm_outer.add(atm_payment_selct, "card2");

        card_selet.setLayout(null);

        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/hdpi.png"))); // NOI18N
        card_selet.add(jLabel59);
        jLabel59.setBounds(110, 200, 70, 50);

        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/phdpi.png"))); // NOI18N
        card_selet.add(jLabel61);
        jLabel61.setBounds(110, 130, 90, 70);

        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/mhdpi.png"))); // NOI18N
        card_selet.add(jLabel60);
        jLabel60.setBounds(110, 250, 70, 70);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel10.setText("Add");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });
        card_selet.add(jLabel10);
        jLabel10.setBounds(90, 140, 460, 50);

        jLabel_paystaionCardSelectHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel_paystaionCardSelectHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardSelectHomeMouseClicked(evt);
            }
        });
        card_selet.add(jLabel_paystaionCardSelectHome);
        jLabel_paystaionCardSelectHome.setBounds(580, 10, 30, 30);

        jLabel_paystaionCardSelectBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCardSelectBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardSelectBackMouseClicked(evt);
            }
        });
        card_selet.add(jLabel_paystaionCardSelectBack);
        jLabel_paystaionCardSelectBack.setBounds(530, 10, 40, 30);

        Page_Title11.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title11.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title11.setForeground(java.awt.Color.white);
        Page_Title11.setText("PayPal");
        card_selet.add(Page_Title11);
        Page_Title11.setBounds(290, 150, 170, 30);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel7.setText("Add");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        card_selet.add(jLabel7);
        jLabel7.setBounds(90, 260, 460, 50);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel8.setText("Add");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        card_selet.add(jLabel8);
        jLabel8.setBounds(90, 200, 460, 50);

        Page_Title12.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title12.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title12.setForeground(java.awt.Color.white);
        Page_Title12.setText("Visa");
        card_selet.add(Page_Title12);
        Page_Title12.setBounds(300, 210, 170, 30);

        Page_Title14.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title14.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title14.setForeground(java.awt.Color.white);
        Page_Title14.setText("Master Card");
        card_selet.add(Page_Title14);
        Page_Title14.setBounds(270, 270, 170, 30);

        Page_Title13.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title13.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title13.setForeground(java.awt.Color.white);
        Page_Title13.setText("Lydia ATM");
        card_selet.add(Page_Title13);
        Page_Title13.setBounds(20, 0, 360, 60);

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel24.setText("Add");
        jLabel24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel24MouseClicked(evt);
            }
        });
        card_selet.add(jLabel24);
        jLabel24.setBounds(0, 0, 630, 60);

        Page_Title31.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title31.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title31.setForeground(java.awt.Color.white);
        Page_Title31.setText("Select a payment method");
        card_selet.add(Page_Title31);
        Page_Title31.setBounds(50, 90, 290, 30);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        card_selet.add(jLabel9);
        jLabel9.setBounds(-10, -700, 695, 1136);

        atm_outer.add(card_selet, "card2");

        card_pay.setLayout(null);

        Page_Title15.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title15.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title15.setForeground(java.awt.Color.white);
        Page_Title15.setText("Pay By Card :");
        card_pay.add(Page_Title15);
        Page_Title15.setBounds(50, 90, 200, 30);

        jLabel_paystaionCardPayHome.setText("HOME");
        jLabel_paystaionCardPayHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayHomeMouseClicked(evt);
            }
        });
        card_pay.add(jLabel_paystaionCardPayHome);
        jLabel_paystaionCardPayHome.setBounds(490, 10, 60, 30);

        jLabel_paystaionCardPayBack.setText("BACK");
        jLabel_paystaionCardPayBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayBackMouseClicked(evt);
            }
        });
        card_pay.add(jLabel_paystaionCardPayBack);
        jLabel_paystaionCardPayBack.setBounds(550, 10, 60, 30);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        card_pay.add(jTextField1);
        jTextField1.setBounds(270, 180, 200, 30);

        cardPayAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cardPayAmountActionPerformed(evt);
            }
        });
        card_pay.add(cardPayAmount);
        cardPayAmount.setBounds(270, 130, 200, 30);

        Page_Title16.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title16.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title16.setForeground(java.awt.Color.white);
        Page_Title16.setText("Cancel The Payment");
        Page_Title16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Page_Title16MouseClicked(evt);
            }
        });
        card_pay.add(Page_Title16);
        Page_Title16.setBounds(260, 300, 170, 30);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/callout-red-bg (2).png"))); // NOI18N
        jLabel11.setText("Add");
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });
        card_pay.add(jLabel11);
        jLabel11.setBounds(130, 300, 410, 30);

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel23.setText("Add");
        jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel23MouseClicked(evt);
            }
        });
        card_pay.add(jLabel23);
        jLabel23.setBounds(0, 0, 630, 60);
        card_pay.add(jLabel16);
        jLabel16.setBounds(280, 130, 200, 30);

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel13.setText("Add");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });
        card_pay.add(jLabel13);
        jLabel13.setBounds(130, 250, 410, 30);

        Page_Title19.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title19.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title19.setForeground(java.awt.Color.white);
        Page_Title19.setText("Accept The Payment");
        card_pay.add(Page_Title19);
        Page_Title19.setBounds(260, 250, 170, 30);

        Page_Title17.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title17.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title17.setForeground(java.awt.Color.white);
        Page_Title17.setText("PIN:");
        card_pay.add(Page_Title17);
        Page_Title17.setBounds(160, 180, 70, 30);

        Page_Title33.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title33.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title33.setForeground(java.awt.Color.white);
        Page_Title33.setText("Amount:");
        card_pay.add(Page_Title33);
        Page_Title33.setBounds(160, 130, 100, 30);

        Page_Title18.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title18.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title18.setForeground(java.awt.Color.white);
        Page_Title18.setText("Lydia ATM");
        card_pay.add(Page_Title18);
        Page_Title18.setBounds(20, 0, 360, 60);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        card_pay.add(jLabel14);
        jLabel14.setBounds(-10, -700, 695, 1136);
        card_pay.add(jLabel17);
        jLabel17.setBounds(280, 130, 200, 30);

        atm_outer.add(card_pay, "card2");

        card_paid.setLayout(null);

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel20MouseClicked(evt);
            }
        });
        card_paid.add(jLabel20);
        jLabel20.setBounds(590, 20, 40, 30);

        jLabel65.setFont(new java.awt.Font("Ubuntu Light", 1, 24)); // NOI18N
        jLabel65.setForeground(java.awt.Color.white);
        jLabel65.setText("$");
        card_paid.add(jLabel65);
        jLabel65.setBounds(360, 300, 20, 30);

        jLabel64.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel64.setText("Add");
        jLabel64.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel64MouseClicked(evt);
            }
        });
        card_paid.add(jLabel64);
        jLabel64.setBounds(60, 160, 500, 30);

        Page_Title20.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title20.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title20.setForeground(java.awt.Color.white);
        Page_Title20.setText("Payment Recived");
        card_paid.add(Page_Title20);
        Page_Title20.setBounds(180, 90, 240, 30);

        jLabel_paystationSmartAccNum.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        jLabel_paystationSmartAccNum.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartAccNum.setText("Smart Account Number");
        jLabel_paystationSmartAccNum.setToolTipText("");
        card_paid.add(jLabel_paystationSmartAccNum);
        jLabel_paystationSmartAccNum.setBounds(70, 190, 200, 30);

        jLabel_paystationSmartAccNum1.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        jLabel_paystationSmartAccNum1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartAccNum1.setText("Smart Account Number");
        jLabel_paystationSmartAccNum1.setToolTipText("");
        card_paid.add(jLabel_paystationSmartAccNum1);
        jLabel_paystationSmartAccNum1.setBounds(360, 190, 210, 30);

        jLabel_paystationSmartCard.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        jLabel_paystationSmartCard.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCard.setText("Smart Card Id");
        jLabel_paystationSmartCard.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCard);
        jLabel_paystationSmartCard.setBounds(70, 230, 140, 30);

        jLabel_paystationSmartCard1.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        jLabel_paystationSmartCard1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCard1.setText("Smart Card Id");
        jLabel_paystationSmartCard1.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCard1);
        jLabel_paystationSmartCard1.setBounds(360, 220, 140, 30);

        jLabel_paystationSmartCardPack.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        jLabel_paystationSmartCardPack.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCardPack.setText("Smart Card package");
        jLabel_paystationSmartCardPack.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCardPack);
        jLabel_paystationSmartCardPack.setBounds(70, 270, 160, 30);

        jLabel_paystationSmartCardPack1.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        jLabel_paystationSmartCardPack1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCardPack1.setText("Smart Card package");
        jLabel_paystationSmartCardPack1.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCardPack1);
        jLabel_paystationSmartCardPack1.setBounds(360, 260, 190, 30);

        jLabel_paystationSmartCardBalance.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        jLabel_paystationSmartCardBalance.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCardBalance.setText("Account Balance");
        jLabel_paystationSmartCardBalance.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCardBalance);
        jLabel_paystationSmartCardBalance.setBounds(70, 310, 140, 30);

        jLabel_paystationSmartCardBalance1.setFont(new java.awt.Font("Ubuntu Light", 1, 24)); // NOI18N
        jLabel_paystationSmartCardBalance1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCardBalance1.setText("Account Balance");
        jLabel_paystationSmartCardBalance1.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCardBalance1);
        jLabel_paystationSmartCardBalance1.setBounds(380, 300, 160, 30);

        jLabel63.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel63.setText("Add");
        jLabel63.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel63MouseClicked(evt);
            }
        });
        card_paid.add(jLabel63);
        jLabel63.setBounds(60, 160, 500, 200);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel15.setText("Add");
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
        });
        card_paid.add(jLabel15);
        jLabel15.setBounds(0, 0, 630, 60);
        card_paid.add(jLabel19);
        jLabel19.setBounds(280, 130, 200, 30);

        Page_Title22.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title22.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title22.setForeground(java.awt.Color.white);
        Page_Title22.setText("Smart Card Information");
        card_paid.add(Page_Title22);
        Page_Title22.setBounds(70, 160, 210, 30);

        Page_Title23.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title23.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title23.setForeground(java.awt.Color.white);
        Page_Title23.setText("Thank You");
        card_paid.add(Page_Title23);
        Page_Title23.setBounds(50, 90, 130, 30);

        Page_Title24.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title24.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title24.setForeground(java.awt.Color.white);
        Page_Title24.setText("Lydia ATM");
        card_paid.add(Page_Title24);
        Page_Title24.setBounds(20, 0, 360, 60);

        Page_Title25.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title25.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title25.setForeground(java.awt.Color.white);
        Page_Title25.setText("Don't Forget to Collect your card");
        card_paid.add(Page_Title25);
        Page_Title25.setBounds(110, 120, 370, 30);

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        card_paid.add(jLabel21);
        jLabel21.setBounds(-10, -700, 695, 1136);

        jLabel62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel62.setText("Add");
        jLabel62.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel62MouseClicked(evt);
            }
        });
        card_paid.add(jLabel62);
        jLabel62.setBounds(0, 0, 630, 60);
        card_paid.add(jLabel22);
        jLabel22.setBounds(280, 130, 200, 30);

        atm_outer.add(card_paid, "card2");

        cash_pay.setLayout(null);

        Page_Title21.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title21.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title21.setForeground(java.awt.Color.white);
        Page_Title21.setText("Amount:");
        cash_pay.add(Page_Title21);
        Page_Title21.setBounds(90, 150, 100, 30);

        jLabel46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue_48.png"))); // NOI18N
        cash_pay.add(jLabel46);
        jLabel46.setBounds(210, 230, 48, 60);

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/mdpi.png"))); // NOI18N
        cash_pay.add(jLabel31);
        jLabel31.setBounds(210, 310, 50, 60);

        jLabel_paystaionCashPayHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel_paystaionCashPayHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCashPayHomeMouseClicked(evt);
            }
        });
        cash_pay.add(jLabel_paystaionCashPayHome);
        jLabel_paystaionCashPayHome.setBounds(590, 10, 30, 30);

        jLabel_paystaionCashPayBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCashPayBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCashPayBackMouseClicked(evt);
            }
        });
        cash_pay.add(jLabel_paystaionCashPayBack);
        jLabel_paystaionCashPayBack.setBounds(540, 10, 40, 30);

        jTextField_paystationCashAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_paystationCashAmountActionPerformed(evt);
            }
        });
        cash_pay.add(jTextField_paystationCashAmount);
        jTextField_paystationCashAmount.setBounds(200, 150, 290, 30);

        Page_Title26.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title26.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title26.setForeground(java.awt.Color.white);
        Page_Title26.setText("Cancel The Payment");
        Page_Title26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Page_Title26MouseClicked(evt);
            }
        });
        cash_pay.add(Page_Title26);
        Page_Title26.setBounds(290, 310, 170, 60);

        paystationInvalidAmount.setBackground(new java.awt.Color(102, 102, 102));
        paystationInvalidAmount.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        paystationInvalidAmount.setForeground(new java.awt.Color(204, 204, 204));
        paystationInvalidAmount.setText("Invalid amount. Re-Enter");
        cash_pay.add(paystationInvalidAmount);
        paystationInvalidAmount.setBounds(200, 180, 290, 20);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/callout-red-bg (2).png"))); // NOI18N
        jLabel12.setText("Add");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        cash_pay.add(jLabel12);
        jLabel12.setBounds(200, 310, 290, 60);

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel27.setText("Add");
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel27MouseClicked(evt);
            }
        });
        cash_pay.add(jLabel27);
        jLabel27.setBounds(0, 0, 630, 60);
        cash_pay.add(jLabel18);
        jLabel18.setBounds(280, 130, 200, 30);

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel28.setText("Add");
        jLabel28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel28MouseClicked(evt);
            }
        });
        cash_pay.add(jLabel28);
        jLabel28.setBounds(200, 230, 290, 60);

        Page_Title27.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title27.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title27.setForeground(java.awt.Color.white);
        Page_Title27.setText("Accept The Payment");
        cash_pay.add(Page_Title27);
        Page_Title27.setBounds(290, 240, 170, 30);

        Page_Title28.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title28.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title28.setForeground(java.awt.Color.white);
        Page_Title28.setText("Pay By Cash");
        cash_pay.add(Page_Title28);
        Page_Title28.setBounds(40, 80, 240, 30);

        Page_Title29.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title29.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title29.setForeground(java.awt.Color.white);
        Page_Title29.setText("Lydia ATM");
        cash_pay.add(Page_Title29);
        Page_Title29.setBounds(20, 0, 360, 60);

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        cash_pay.add(jLabel29);
        jLabel29.setBounds(-10, -700, 695, 1230);
        cash_pay.add(jLabel30);
        jLabel30.setBounds(280, 130, 200, 30);

        atm_outer.add(cash_pay, "card2");

        select_package.setLayout(null);

        Page_Title39.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title39.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title39.setForeground(java.awt.Color.white);
        Page_Title39.setText("Mega package");
        select_package.add(Page_Title39);
        Page_Title39.setBounds(320, 270, 170, 30);

        jLabel_paystaionPackageHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel_paystaionPackageHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageHomeMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystaionPackageHome);
        jLabel_paystaionPackageHome.setBounds(590, 10, 30, 30);

        jLabel_paystaionPackageBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionPackageBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageBackMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystaionPackageBack);
        jLabel_paystaionPackageBack.setBounds(550, 10, 30, 30);

        jLabel_paystationMegaPack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationMegaPack.setText("Add");
        jLabel_paystationMegaPack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationMegaPackMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystationMegaPack);
        jLabel_paystationMegaPack.setBounds(310, 260, 210, 100);

        Page_Title34.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title34.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title34.setForeground(java.awt.Color.white);
        Page_Title34.setText("Night Package");
        select_package.add(Page_Title34);
        Page_Title34.setBounds(60, 150, 170, 30);

        Page_Title36.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title36.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title36.setForeground(java.awt.Color.white);
        Page_Title36.setText("Budject Package");
        select_package.add(Page_Title36);
        Page_Title36.setBounds(60, 270, 170, 30);

        jLabel_paystationBudjectPack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationBudjectPack.setText("Add");
        jLabel_paystationBudjectPack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationBudjectPackMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel_paystationBudjectPackMouseEntered(evt);
            }
        });
        select_package.add(jLabel_paystationBudjectPack);
        jLabel_paystationBudjectPack.setBounds(50, 260, 220, 100);

        jLabel_paystationNightPack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack.setText("Add");
        jLabel_paystationNightPack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPackMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystationNightPack);
        jLabel_paystationNightPack.setBounds(50, 140, 220, 100);

        Page_Title35.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title35.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title35.setForeground(java.awt.Color.white);
        Page_Title35.setText("Day Package");
        select_package.add(Page_Title35);
        Page_Title35.setBounds(320, 150, 170, 30);

        Page_Title37.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title37.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title37.setForeground(java.awt.Color.white);
        Page_Title37.setText("Lydia ATM");
        select_package.add(Page_Title37);
        Page_Title37.setBounds(20, 0, 360, 60);

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel34.setText("Add");
        jLabel34.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel34MouseClicked(evt);
            }
        });
        select_package.add(jLabel34);
        jLabel34.setBounds(0, 0, 630, 60);

        jLabel_paystationDayPack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationDayPack.setText("Add");
        jLabel_paystationDayPack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationDayPackMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystationDayPack);
        jLabel_paystationDayPack.setBounds(310, 140, 210, 100);

        Page_Title38.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title38.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title38.setForeground(java.awt.Color.white);
        Page_Title38.setText("Select a package for smart card");
        select_package.add(Page_Title38);
        Page_Title38.setBounds(50, 90, 370, 30);

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        select_package.add(jLabel35);
        jLabel35.setBounds(-10, -700, 695, 1136);

        atm_outer.add(select_package, "card2");

        jPanel_selectSmartCradOptions.setLayout(null);

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel37.setText("Add");
        jLabel37.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel37MouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel37);
        jLabel37.setBounds(70, 160, 460, 60);

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel38.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel38MouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel38);
        jLabel38.setBounds(70, 240, 460, 60);

        jLabel_paystaionSmartCardOptionHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel_paystaionSmartCardOptionHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionSmartCardOptionHomeMouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel_paystaionSmartCardOptionHome);
        jLabel_paystaionSmartCardOptionHome.setBounds(590, 10, 40, 30);

        jLabel_paystaionSmartCardOptionBack.setFont(new java.awt.Font("Ubuntu Light", 0, 12)); // NOI18N
        jLabel_paystaionSmartCardOptionBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionSmartCardOptionBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionSmartCardOptionBackMouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel_paystaionSmartCardOptionBack);
        jLabel_paystaionSmartCardOptionBack.setBounds(550, 10, 30, 30);

        Page_Title40.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title40.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title40.setForeground(java.awt.Color.white);
        Page_Title40.setText("Select a option");
        jPanel_selectSmartCradOptions.add(Page_Title40);
        Page_Title40.setBounds(50, 90, 170, 30);

        jLable_smartCardTopUp.setBackground(new java.awt.Color(102, 102, 102));
        jLable_smartCardTopUp.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        jLable_smartCardTopUp.setForeground(java.awt.Color.white);
        jLable_smartCardTopUp.setText("                                      Top-Up");
        jPanel_selectSmartCradOptions.add(jLable_smartCardTopUp);
        jLable_smartCardTopUp.setBounds(100, 260, 230, 30);

        jLable_newSmartCard.setBackground(new java.awt.Color(102, 102, 102));
        jLable_newSmartCard.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        jLable_newSmartCard.setForeground(java.awt.Color.white);
        jLable_newSmartCard.setText("                                 New Smart Card");
        jLable_newSmartCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLable_newSmartCardMouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLable_newSmartCard);
        jLable_newSmartCard.setBounds(100, 170, 460, 30);

        Page_Title43.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title43.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title43.setForeground(java.awt.Color.white);
        Page_Title43.setText("Lydia ATM ");
        jPanel_selectSmartCradOptions.add(Page_Title43);
        Page_Title43.setBounds(20, 0, 260, 60);

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel39.setText("Add");
        jLabel39.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel39MouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel39);
        jLabel39.setBounds(0, 0, 630, 60);

        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        jPanel_selectSmartCradOptions.add(jLabel40);
        jLabel40.setBounds(-10, -700, 695, 1200);

        atm_outer.add(jPanel_selectSmartCradOptions, "card2");

        enterCardId.setLayout(null);

        paystaionCardIdInvalid.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardIdInvalid.setFont(new java.awt.Font("Ubuntu Light", 0, 14)); // NOI18N
        paystaionCardIdInvalid.setForeground(java.awt.Color.white);
        paystaionCardIdInvalid.setText("Invalid credentials. Try again!");
        enterCardId.add(paystaionCardIdInvalid);
        paystaionCardIdInvalid.setBounds(220, 200, 210, 50);

        paystaionCardId2.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId2.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        paystaionCardId2.setForeground(java.awt.Color.white);
        paystaionCardId2.setText("Enter your Card ID");
        enterCardId.add(paystaionCardId2);
        paystaionCardId2.setBounds(50, 90, 220, 30);

        jLabel_paystaionCardPayHome1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel_paystaionCardPayHome1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayHome1MouseClicked(evt);
            }
        });
        enterCardId.add(jLabel_paystaionCardPayHome1);
        jLabel_paystaionCardPayHome1.setBounds(560, 10, 40, 30);

        jLabel_paystaionCardPayBack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCardPayBack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayBack1MouseClicked(evt);
            }
        });
        enterCardId.add(jLabel_paystaionCardPayBack1);
        jLabel_paystaionCardPayBack1.setBounds(520, 10, 30, 30);

        paystaionCardIdInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystaionCardIdInputActionPerformed(evt);
            }
        });
        enterCardId.add(paystaionCardIdInput);
        paystaionCardIdInput.setBounds(210, 140, 240, 30);

        paystaionCardPinTopUp.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardPinTopUp.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        paystaionCardPinTopUp.setForeground(java.awt.Color.white);
        paystaionCardPinTopUp.setText("PIN");
        enterCardId.add(paystaionCardPinTopUp);
        paystaionCardPinTopUp.setBounds(100, 180, 100, 21);

        paystaionCardPinTopUp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystaionCardPinTopUp1ActionPerformed(evt);
            }
        });
        enterCardId.add(paystaionCardPinTopUp1);
        paystaionCardPinTopUp1.setBounds(210, 180, 240, 30);

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel32.setText("Add");
        jLabel32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel32MouseClicked(evt);
            }
        });
        enterCardId.add(jLabel32);
        jLabel32.setBounds(0, 0, 630, 60);
        enterCardId.add(jLabel33);
        jLabel33.setBounds(280, 130, 200, 30);

        paystaionCardIdEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        paystaionCardIdEnter.setText("Add");
        paystaionCardIdEnter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paystaionCardIdEnterMouseClicked(evt);
            }
        });
        enterCardId.add(paystaionCardIdEnter);
        paystaionCardIdEnter.setBounds(210, 270, 240, 50);

        paystaionCardIdEnterText.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardIdEnterText.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        paystaionCardIdEnterText.setForeground(java.awt.Color.white);
        paystaionCardIdEnterText.setText("Enter ");
        enterCardId.add(paystaionCardIdEnterText);
        paystaionCardIdEnterText.setBounds(310, 280, 60, 30);

        paystaionCardId.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        paystaionCardId.setForeground(java.awt.Color.white);
        paystaionCardId.setText("Card ID");
        enterCardId.add(paystaionCardId);
        paystaionCardId.setBounds(100, 140, 100, 21);

        Page_Title47.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title47.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title47.setForeground(java.awt.Color.white);
        Page_Title47.setText("Lydia ATM");
        enterCardId.add(Page_Title47);
        Page_Title47.setBounds(20, 0, 360, 60);

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        enterCardId.add(jLabel41);
        jLabel41.setBounds(-10, -700, 695, 1136);
        enterCardId.add(jLabel42);
        jLabel42.setBounds(280, 130, 200, 30);

        atm_outer.add(enterCardId, "card2");

        enterCardPin.setLayout(null);

        paystaionCardPinNoMatch.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardPinNoMatch.setFont(new java.awt.Font("Ubuntu Light", 0, 14)); // NOI18N
        paystaionCardPinNoMatch.setForeground(java.awt.Color.white);
        paystaionCardPinNoMatch.setText("PIN numbers doesn't match ");
        enterCardPin.add(paystaionCardPinNoMatch);
        paystaionCardPinNoMatch.setBounds(250, 210, 210, 50);

        paystaionCardId3.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId3.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        paystaionCardId3.setForeground(java.awt.Color.white);
        paystaionCardId3.setText("Enter your PIN for card");
        enterCardPin.add(paystaionCardId3);
        paystaionCardId3.setBounds(50, 80, 310, 30);

        jLabel_paystaionCardPayHome2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel_paystaionCardPayHome2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayHome2MouseClicked(evt);
            }
        });
        enterCardPin.add(jLabel_paystaionCardPayHome2);
        jLabel_paystaionCardPayHome2.setBounds(580, 10, 40, 30);

        jLabel_paystaionCardPayBack2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCardPayBack2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayBack2MouseClicked(evt);
            }
        });
        enterCardPin.add(jLabel_paystaionCardPayBack2);
        jLabel_paystaionCardPayBack2.setBounds(540, 10, 30, 30);

        paystaionCardPinInput2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystaionCardPinInput2ActionPerformed(evt);
            }
        });
        enterCardPin.add(paystaionCardPinInput2);
        paystaionCardPinInput2.setBounds(250, 180, 270, 30);

        paystaionCardPinInput1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystaionCardPinInput1ActionPerformed(evt);
            }
        });
        enterCardPin.add(paystaionCardPinInput1);
        paystaionCardPinInput1.setBounds(250, 140, 270, 30);

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel36.setText("Add");
        jLabel36.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel36MouseClicked(evt);
            }
        });
        enterCardPin.add(jLabel36);
        jLabel36.setBounds(0, 0, 630, 60);
        enterCardPin.add(jLabel43);
        jLabel43.setBounds(280, 130, 200, 30);

        paystaionCardPinSubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        paystaionCardPinSubmit.setText("Add");
        paystaionCardPinSubmit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paystaionCardPinSubmitMouseClicked(evt);
            }
        });
        enterCardPin.add(paystaionCardPinSubmit);
        paystaionCardPinSubmit.setBounds(250, 260, 270, 50);

        paystaionCardIdEnterText1.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardIdEnterText1.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        paystaionCardIdEnterText1.setForeground(java.awt.Color.white);
        paystaionCardIdEnterText1.setText("submit");
        enterCardPin.add(paystaionCardIdEnterText1);
        paystaionCardIdEnterText1.setBounds(340, 270, 80, 30);

        paystaionCardId1.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId1.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        paystaionCardId1.setForeground(java.awt.Color.white);
        paystaionCardId1.setText("RE-ENTER PIN");
        enterCardPin.add(paystaionCardId1);
        paystaionCardId1.setBounds(110, 180, 180, 28);

        paystaionCardId4.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId4.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        paystaionCardId4.setForeground(java.awt.Color.white);
        paystaionCardId4.setText("PIN");
        enterCardPin.add(paystaionCardId4);
        paystaionCardId4.setBounds(110, 140, 50, 21);

        Page_Title48.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title48.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title48.setForeground(java.awt.Color.white);
        Page_Title48.setText("Lydia ATM");
        enterCardPin.add(Page_Title48);
        Page_Title48.setBounds(20, 0, 360, 60);

        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        enterCardPin.add(jLabel44);
        jLabel44.setBounds(-10, -700, 695, 1250);
        enterCardPin.add(jLabel45);
        jLabel45.setBounds(280, 130, 200, 30);

        atm_outer.add(enterCardPin, "card2");

        paystationPassiveCardNormal.setLayout(null);

        passiveCardNormalNext.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        passiveCardNormalNext.setForeground(java.awt.Color.white);
        passiveCardNormalNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-bueSmall.png"))); // NOI18N
        passiveCardNormalNext.setText("      Next");
        passiveCardNormalNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                passiveCardNormalNextMouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(passiveCardNormalNext);
        passiveCardNormalNext.setBounds(410, 270, 200, 50);

        passivceCardNormalCheck.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        passivceCardNormalCheck.setForeground(java.awt.Color.white);
        passivceCardNormalCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-bueSmall.png"))); // NOI18N
        passivceCardNormalCheck.setText("      check");
        passivceCardNormalCheck.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                passivceCardNormalCheckMouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(passivceCardNormalCheck);
        passivceCardNormalCheck.setBounds(70, 270, 160, 50);

        passiveCardTotal.setFont(new java.awt.Font("Ubuntu Light", 1, 26)); // NOI18N
        passiveCardTotal.setForeground(java.awt.Color.white);
        passiveCardTotal.setText("amount");
        paystationPassiveCardNormal.add(passiveCardTotal);
        passiveCardTotal.setBounds(410, 160, 120, 30);

        jLabel49.setFont(new java.awt.Font("Ubuntu Light", 0, 20)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(255, 255, 255));
        jLabel49.setText("Destination");
        paystationPassiveCardNormal.add(jLabel49);
        jLabel49.setBounds(60, 190, 150, 30);

        passiveCardSourse.setForeground(java.awt.Color.white);
        passiveCardSourse.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "select", "Pettah" }));
        paystationPassiveCardNormal.add(passiveCardSourse);
        passiveCardSourse.setBounds(60, 150, 140, 25);

        passiveCardDestination.setForeground(java.awt.Color.white);
        passiveCardDestination.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "select", "Panadura" }));
        paystationPassiveCardNormal.add(passiveCardDestination);
        passiveCardDestination.setBounds(60, 230, 150, 25);

        jLabel_paystaionPaymentHome1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel_paystaionPaymentHome1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPaymentHome1MouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(jLabel_paystaionPaymentHome1);
        jLabel_paystaionPaymentHome1.setBounds(590, 10, 40, 30);

        jLabel_paystaionPaymentBack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionPaymentBack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPaymentBack1MouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(jLabel_paystaionPaymentBack1);
        jLabel_paystaionPaymentBack1.setBounds(550, 10, 30, 30);

        Page_Title53.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title53.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title53.setForeground(java.awt.Color.white);
        Page_Title53.setText("Select Your Souse And Destination to predict Price");
        paystationPassiveCardNormal.add(Page_Title53);
        Page_Title53.setBounds(20, 70, 540, 30);

        jLabel66.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel66.setText("Add");
        jLabel66.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel66MouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(jLabel66);
        jLabel66.setBounds(340, 270, 270, 50);

        jLabel68.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel68.setText("Add");
        jLabel68.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel68MouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(jLabel68);
        jLabel68.setBounds(60, 270, 170, 50);

        Page_Title44.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title44.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title44.setForeground(java.awt.Color.white);
        Page_Title44.setText("Lydia ATM ");
        paystationPassiveCardNormal.add(Page_Title44);
        Page_Title44.setBounds(20, 0, 260, 60);

        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel47.setText("Add");
        jLabel47.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel47MouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(jLabel47);
        jLabel47.setBounds(0, 0, 630, 60);

        Page_Title45.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title45.setFont(new java.awt.Font("Ubuntu Light", 0, 20)); // NOI18N
        Page_Title45.setForeground(java.awt.Color.white);
        Page_Title45.setText("Sourse");
        paystationPassiveCardNormal.add(Page_Title45);
        Page_Title45.setBounds(60, 110, 120, 30);

        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        paystationPassiveCardNormal.add(jLabel48);
        jLabel48.setBounds(-10, -700, 695, 1220);

        jLabel_payStationBack1.setText("Back");
        paystationPassiveCardNormal.add(jLabel_payStationBack1);
        jLabel_payStationBack1.setBounds(50, 300, 80, 40);

        jLabel67.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel67.setText("Add");
        jLabel67.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel67MouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(jLabel67);
        jLabel67.setBounds(220, 200, 170, 50);

        atm_outer.add(paystationPassiveCardNormal, "card2");

        passiveCardPaid.setLayout(null);

        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel52.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel52MouseClicked(evt);
            }
        });
        passiveCardPaid.add(jLabel52);
        jLabel52.setBounds(590, 10, 40, 30);

        Page_Title46.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title46.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title46.setForeground(java.awt.Color.white);
        Page_Title46.setText("Payment Recived");
        passiveCardPaid.add(Page_Title46);
        Page_Title46.setBounds(200, 160, 240, 30);

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel50.setText("Add");
        jLabel50.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel50MouseClicked(evt);
            }
        });
        passiveCardPaid.add(jLabel50);
        jLabel50.setBounds(0, 0, 630, 60);
        passiveCardPaid.add(jLabel51);
        jLabel51.setBounds(280, 130, 200, 30);

        Page_Title50.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title50.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title50.setForeground(java.awt.Color.white);
        Page_Title50.setText("Thank You");
        passiveCardPaid.add(Page_Title50);
        Page_Title50.setBounds(240, 120, 130, 30);

        Page_Title51.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title51.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title51.setForeground(java.awt.Color.white);
        Page_Title51.setText("Lydia ATM");
        passiveCardPaid.add(Page_Title51);
        Page_Title51.setBounds(20, 0, 360, 60);

        Page_Title52.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title52.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title52.setForeground(java.awt.Color.white);
        Page_Title52.setText("Don't Forget to Collect your ticket");
        passiveCardPaid.add(Page_Title52);
        Page_Title52.setBounds(130, 200, 370, 30);

        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        passiveCardPaid.add(jLabel53);
        jLabel53.setBounds(-10, -700, 695, 1200);
        passiveCardPaid.add(jLabel54);
        jLabel54.setBounds(280, 130, 200, 30);

        atm_outer.add(passiveCardPaid, "card2");

        passivePackages.setLayout(null);

        jLabel_paystaionPackageHome1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-SHomrmall.png"))); // NOI18N
        jLabel_paystaionPackageHome1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageHome1MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystaionPackageHome1);
        jLabel_paystaionPackageHome1.setBounds(570, 10, 30, 30);

        jLabel_paystaionPackageBack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionPackageBack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageBack1MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystaionPackageBack1);
        jLabel_paystaionPackageBack1.setBounds(530, 10, 30, 30);

        Page_Title54.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title54.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title54.setForeground(java.awt.Color.white);
        Page_Title54.setText("Normal");
        passivePackages.add(Page_Title54);
        Page_Title54.setBounds(60, 160, 80, 30);

        jLabel_paystationNightPack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack1.setText("Add");
        jLabel_paystationNightPack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPack1MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationNightPack1);
        jLabel_paystationNightPack1.setBounds(50, 160, 220, 100);

        Page_Title56.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title56.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title56.setForeground(java.awt.Color.white);
        Page_Title56.setText("One day");
        passivePackages.add(Page_Title56);
        Page_Title56.setBounds(320, 160, 80, 30);

        Page_Title57.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title57.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title57.setForeground(java.awt.Color.white);
        Page_Title57.setText("Lydia ATM");
        passivePackages.add(Page_Title57);
        Page_Title57.setBounds(20, 0, 360, 60);

        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel55.setText("Add");
        jLabel55.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel55MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel55);
        jLabel55.setBounds(0, 0, 630, 60);

        jLabel_paystationDayPack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationDayPack1.setText("Add");
        jLabel_paystationDayPack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationDayPack1MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationDayPack1);
        jLabel_paystationDayPack1.setBounds(310, 160, 220, 100);

        Page_Title58.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title58.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title58.setForeground(java.awt.Color.white);
        Page_Title58.setText("Select a package for smart card");
        passivePackages.add(Page_Title58);
        Page_Title58.setBounds(50, 90, 370, 30);

        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/2013-06-13-13.13.33.png"))); // NOI18N
        passivePackages.add(jLabel56);
        jLabel56.setBounds(-10, -700, 695, 1270);

        atm_outer.add(passivePackages, "card2");

        getContentPane().add(atm_outer);
        atm_outer.setBounds(0, 0, 730, 400);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked

        currentPassiveCard = true;
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(passivePackages);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(jPanel_selectSmartCradOptions);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked

        if (currentPassiveCard == true) { //if it's a passive card
            try {

                issueCard(0);
            } catch (ParseException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(cash_pay);
        atm_outer.repaint();
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
       if (currentPassiveCard == true) { //if it's a passive card
            try {

                issueCard(0);
            } catch (ParseException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(card_selet);
        atm_outer.repaint();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(card_selet);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(card_selet);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(card_pay);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        try {
            acceptPayment(Double.parseDouble(cardPayAmount.getText()));
        } catch (NumberFormatException e) {
            paystationInvalidAmount.setVisible(true);
        }
        cardPayAmount.setText("");
        paystaionCardIdInput.setText("");
        paystaionCardPinTopUp1.setText("");
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void cardPayAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cardPayAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cardPayAmountActionPerformed

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked


    }//GEN-LAST:event_jLabel11MouseClicked

    private void jLabel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel15MouseClicked

    private void jLabel20MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel20MouseClicked
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel20MouseClicked

    private void jLabel23MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel23MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel23MouseClicked

    private void jLabel24MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel24MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel24MouseClicked

    private void jLabel25MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel25MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel25MouseClicked

    private void jLabel26MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel26MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel26MouseClicked

    private void Page_Title16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Page_Title16MouseClicked
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_Page_Title16MouseClicked

    private void jTextField_paystationCashAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_paystationCashAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_paystationCashAmountActionPerformed

    private void Page_Title26MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Page_Title26MouseClicked
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_Page_Title26MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel27MouseClicked

    private void jLabel28MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel28MouseClicked

        try {
            acceptPayment(Double.parseDouble(jTextField_paystationCashAmount.getText()));
        } catch (NumberFormatException e) {
            paystationInvalidAmount.setVisible(true);
        }
        jTextField_paystationCashAmount.setText("");
        paystaionCardIdInput.setText("");
        paystaionCardPinTopUp1.setText("");


    }//GEN-LAST:event_jLabel28MouseClicked

    private void jLabel_paystationNightPackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPackMouseClicked

        selectPackage("Night");//select the package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel_paystationNightPackMouseClicked

    private void jLabel34MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel34MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel34MouseClicked

    private void jLabel37MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel37MouseClicked
        // TODO add your handling code here:
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(enterCardId);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel37MouseClicked

    private void jLabel38MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel38MouseClicked

//        try {
//            issueCard();
//            // TODO add your handling code here:
//            // removing pane
//            atm_outer.removeAll();
//            atm_outer.repaint();
//            atm_outer.revalidate();
//            //adding pane
//            atm_outer.add(select_package);
//            atm_outer.repaint();
//            atm_outer.revalidate();
//        } catch (ParseException ex) {
//            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
//        }
        //removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(enterCardPin);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel38MouseClicked

    private void jLabel39MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel39MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel39MouseClicked

    private void jLable_newSmartCardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLable_newSmartCardMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLable_newSmartCardMouseClicked

    private void jLabel_paystaionPackageBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageBackMouseClicked
        // TODO add your handling code here:
        if (currentPassiveCard == false) {
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(jPanel_selectSmartCradOptions);
            atm_outer.repaint();
            atm_outer.revalidate();
        } else {
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(jPanel_selectSmartCradOptions);
            atm_outer.repaint();
            atm_outer.revalidate();

        }


    }//GEN-LAST:event_jLabel_paystaionPackageBackMouseClicked

    private void jLabel_paystaionPackageHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageHomeMouseClicked
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionPackageHomeMouseClicked

    private void jLabel_paystaionSmartCardOptionHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionSmartCardOptionHomeMouseClicked
        // TODO add your handling code here:
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionSmartCardOptionHomeMouseClicked

    private void jLabel_paystaionSmartCardOptionBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionSmartCardOptionBackMouseClicked
        // TODO add your handling code here:
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionSmartCardOptionBackMouseClicked

    private void jLabel_paystaionCashPayHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCashPayHomeMouseClicked

        jTextField_paystationCashAmount.setText("");
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();


    }//GEN-LAST:event_jLabel_paystaionCashPayHomeMouseClicked

    private void jLabel_paystaionCashPayBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCashPayBackMouseClicked
        // TODO add your handling code here:
        jTextField_paystationCashAmount.setText("");

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionCashPayBackMouseClicked

    private void jLabel_paystaionCardPayHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayHomeMouseClicked
        // TODO add your handling code here:
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionCardPayHomeMouseClicked

    private void jLabel_paystaionCardPayBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayBackMouseClicked
        // TODO add your handling code here:
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionCardPayBackMouseClicked

    private void jLabel_paystaionCardSelectHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardSelectHomeMouseClicked
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel_paystaionCardSelectHomeMouseClicked

    private void jLabel_paystaionCardSelectBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardSelectBackMouseClicked
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionCardSelectBackMouseClicked

    private void jLabel_paystaionPaymentHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPaymentHomeMouseClicked
        // TODO add your handling code here:
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionPaymentHomeMouseClicked

    private void jLabel_paystaionPaymentBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPaymentBackMouseClicked
        // TODO add your handling code here:
        // TODO add your handling code here:

        if (currentPassiveCard == false) { //if it's a smart card
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(select_package);
            atm_outer.repaint();
            atm_outer.revalidate();
        } else { //if it's a passive card
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(passivePackages);
            atm_outer.repaint();
            atm_outer.revalidate();
        }

    }//GEN-LAST:event_jLabel_paystaionPaymentBackMouseClicked

    private void jLabel_paystationDayPackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationDayPackMouseClicked

        selectPackage("Day");//select the package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();

        System.out.println("day button clicked");
    }//GEN-LAST:event_jLabel_paystationDayPackMouseClicked

    private void jLabel_paystaionCardPayHome1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayHome1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystaionCardPayHome1MouseClicked

    private void jLabel_paystaionCardPayBack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayBack1MouseClicked
        // TODO add your handling code here:

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(jPanel_selectSmartCradOptions);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel_paystaionCardPayBack1MouseClicked

    private void paystaionCardIdInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paystaionCardIdInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paystaionCardIdInputActionPerformed

    private void jLabel32MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel32MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel32MouseClicked

    private void paystaionCardIdEnterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paystaionCardIdEnterMouseClicked
        // TODO add your handling code here:
        try {
            validateSmartCardId(paystaionCardIdInput.getText(), Integer.parseInt(paystaionCardPinTopUp1.getText()));
        } catch (NumberFormatException e) {
            paystaionCardIdInvalid.setVisible(true);
        }

        //System.out.println("card number sc acc no: "+currentSmartCard.getAcountNumber());
    }//GEN-LAST:event_paystaionCardIdEnterMouseClicked

    private void jLabel_paystaionCardPayHome2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayHome2MouseClicked
        // TODO add your handling code here:
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionCardPayHome2MouseClicked

    private void jLabel_paystaionCardPayBack2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayBack2MouseClicked
        // TODO add your handling code here:
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(jPanel_selectSmartCradOptions);
        atm_outer.repaint();
        atm_outer.revalidate();


    }//GEN-LAST:event_jLabel_paystaionCardPayBack2MouseClicked

    private void paystaionCardPinInput2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paystaionCardPinInput2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paystaionCardPinInput2ActionPerformed

    private void jLabel36MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel36MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel36MouseClicked

    private void paystaionCardPinSubmitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paystaionCardPinSubmitMouseClicked
        // TODO add your handling code here:
        if ((!paystaionCardPinInput1.getText().isEmpty()) && (!paystaionCardPinInput1.getText().isEmpty())) {
            try {
                validatePin(Integer.parseInt(paystaionCardPinInput1.getText()), Integer.parseInt(paystaionCardPinInput2.getText()));

            } catch (NumberFormatException e) {
                System.out.println("Only Numbers allowed");
                paystaionCardPinNoMatch.setVisible(true);
                paystaionCardPinNoMatch.setText("Only Numbers allowed");
            }
        } else {
            System.out.println("Fill all fields");
            paystaionCardPinNoMatch.setVisible(true);
            paystaionCardPinNoMatch.setText("Fill all fields");
        }

        paystaionCardPinInput1.setText("");
        paystaionCardPinInput2.setText("");


    }//GEN-LAST:event_paystaionCardPinSubmitMouseClicked

    private void paystaionCardPinInput1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paystaionCardPinInput1ActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_paystaionCardPinInput1ActionPerformed

    private void paystaionCardPinTopUp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paystaionCardPinTopUp1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paystaionCardPinTopUp1ActionPerformed

    private void jLabel_paystaionPaymentHome1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPaymentHome1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystaionPaymentHome1MouseClicked

    private void jLabel_paystaionPaymentBack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPaymentBack1MouseClicked
        // TODO add your handling code here:
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(passivePackages);
        atm_outer.repaint();
        atm_outer.revalidate();


    }//GEN-LAST:event_jLabel_paystaionPaymentBack1MouseClicked

    private void jLabel47MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel47MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel47MouseClicked

    private void jLabel50MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel50MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel50MouseClicked

    private void jLabel52MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel52MouseClicked

        // TODO add your handling code here:
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel52MouseClicked

    private void jLabel_paystationMegaPackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationMegaPackMouseClicked

        selectPackage("Mega");//select the package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystationMegaPackMouseClicked

    private void jLabel_paystationBudjectPackMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationBudjectPackMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationBudjectPackMouseEntered

    private void jLabel_paystationBudjectPackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationBudjectPackMouseClicked

        selectPackage("Budject");//select the package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystationBudjectPackMouseClicked

    private void jLabel_paystaionPackageHome1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageHome1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystaionPackageHome1MouseClicked

    private void jLabel_paystaionPackageBack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageBack1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystaionPackageBack1MouseClicked

    private void jLabel55MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel55MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel55MouseClicked

    private void jLabel_paystationDayPack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationDayPack1MouseClicked
        // TODO add your handling code here:
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();


    }//GEN-LAST:event_jLabel_paystationDayPack1MouseClicked

    private void jLabel_paystationNightPack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPack1MouseClicked
        // TODO add your handling code here:

        //normal package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(paystationPassiveCardNormal);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystationNightPack1MouseClicked

    private void passivceCardNormalCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_passivceCardNormalCheckMouseClicked
        // TODO add your handling code here:
        if((passiveCardSourse.getSelectedIndex() > 0 )&& (passiveCardDestination.getSelectedIndex() > 0))
        {
      double distance= DistanceChart.getInstance().getDistance((String) passiveCardSourse.getSelectedItem(), (String) passiveCardDestination.getSelectedItem());
      passiveChargeAmount = calculateCharge(distance);
      passiveCardTotal.setText(String.valueOf(passiveChargeAmount));
      passiveCardNormalNext.setVisible(true);
      
        }
      
    }//GEN-LAST:event_passivceCardNormalCheckMouseClicked

    private void passiveCardNormalNextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_passiveCardNormalNextMouseClicked
        // TODO add your handling code here:
        
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_passiveCardNormalNextMouseClicked

    private void jLabel62MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel62MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel62MouseClicked

    private void jLabel63MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel63MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel63MouseClicked

    private void jLabel64MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel64MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel64MouseClicked

    private void jLabel66MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel66MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel66MouseClicked

    private void jLabel67MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel67MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel67MouseClicked

    private void jLabel68MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel68MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel68MouseClicked

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
            java.util.logging.Logger.getLogger(PayStationUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayStationUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayStationUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayStationUi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
                new PayStationUi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Page_Title10;
    private javax.swing.JLabel Page_Title11;
    private javax.swing.JLabel Page_Title12;
    private javax.swing.JLabel Page_Title13;
    private javax.swing.JLabel Page_Title14;
    private javax.swing.JLabel Page_Title15;
    private javax.swing.JLabel Page_Title16;
    private javax.swing.JLabel Page_Title17;
    private javax.swing.JLabel Page_Title18;
    private javax.swing.JLabel Page_Title19;
    private javax.swing.JLabel Page_Title20;
    private javax.swing.JLabel Page_Title21;
    private javax.swing.JLabel Page_Title22;
    private javax.swing.JLabel Page_Title23;
    private javax.swing.JLabel Page_Title24;
    private javax.swing.JLabel Page_Title25;
    private javax.swing.JLabel Page_Title26;
    private javax.swing.JLabel Page_Title27;
    private javax.swing.JLabel Page_Title28;
    private javax.swing.JLabel Page_Title29;
    private javax.swing.JLabel Page_Title30;
    private javax.swing.JLabel Page_Title31;
    private javax.swing.JLabel Page_Title32;
    private javax.swing.JLabel Page_Title33;
    private javax.swing.JLabel Page_Title34;
    private javax.swing.JLabel Page_Title35;
    private javax.swing.JLabel Page_Title36;
    private javax.swing.JLabel Page_Title37;
    private javax.swing.JLabel Page_Title38;
    private javax.swing.JLabel Page_Title39;
    private javax.swing.JLabel Page_Title40;
    private javax.swing.JLabel Page_Title43;
    private javax.swing.JLabel Page_Title44;
    private javax.swing.JLabel Page_Title45;
    private javax.swing.JLabel Page_Title46;
    private javax.swing.JLabel Page_Title47;
    private javax.swing.JLabel Page_Title48;
    private javax.swing.JLabel Page_Title5;
    private javax.swing.JLabel Page_Title50;
    private javax.swing.JLabel Page_Title51;
    private javax.swing.JLabel Page_Title52;
    private javax.swing.JLabel Page_Title53;
    private javax.swing.JLabel Page_Title54;
    private javax.swing.JLabel Page_Title56;
    private javax.swing.JLabel Page_Title57;
    private javax.swing.JLabel Page_Title58;
    private javax.swing.JLabel Page_Title6;
    private javax.swing.JLabel Page_Title7;
    private javax.swing.JLabel Page_Title8;
    private javax.swing.JLabel Page_Title9;
    private javax.swing.JPanel atm_home;
    private javax.swing.JPanel atm_outer;
    private javax.swing.JPanel atm_payment_selct;
    private javax.swing.JTextField cardPayAmount;
    private javax.swing.JPanel card_paid;
    private javax.swing.JPanel card_pay;
    private javax.swing.JPanel card_selet;
    private javax.swing.JPanel cash_pay;
    private javax.swing.JPanel enterCardId;
    private javax.swing.JPanel enterCardPin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_payStationBack;
    private javax.swing.JLabel jLabel_payStationBack1;
    private javax.swing.JLabel jLabel_paystaionCardPayBack;
    private javax.swing.JLabel jLabel_paystaionCardPayBack1;
    private javax.swing.JLabel jLabel_paystaionCardPayBack2;
    private javax.swing.JLabel jLabel_paystaionCardPayHome;
    private javax.swing.JLabel jLabel_paystaionCardPayHome1;
    private javax.swing.JLabel jLabel_paystaionCardPayHome2;
    private javax.swing.JLabel jLabel_paystaionCardSelectBack;
    private javax.swing.JLabel jLabel_paystaionCardSelectHome;
    private javax.swing.JLabel jLabel_paystaionCashPayBack;
    private javax.swing.JLabel jLabel_paystaionCashPayHome;
    private javax.swing.JLabel jLabel_paystaionPackageBack;
    private javax.swing.JLabel jLabel_paystaionPackageBack1;
    private javax.swing.JLabel jLabel_paystaionPackageHome;
    private javax.swing.JLabel jLabel_paystaionPackageHome1;
    private javax.swing.JLabel jLabel_paystaionPaymentBack;
    private javax.swing.JLabel jLabel_paystaionPaymentBack1;
    private javax.swing.JLabel jLabel_paystaionPaymentHome;
    private javax.swing.JLabel jLabel_paystaionPaymentHome1;
    private javax.swing.JLabel jLabel_paystaionSmartCardOptionBack;
    private javax.swing.JLabel jLabel_paystaionSmartCardOptionHome;
    private javax.swing.JLabel jLabel_paystationBudjectPack;
    private javax.swing.JLabel jLabel_paystationDayPack;
    private javax.swing.JLabel jLabel_paystationDayPack1;
    private javax.swing.JLabel jLabel_paystationMegaPack;
    private javax.swing.JLabel jLabel_paystationNightPack;
    private javax.swing.JLabel jLabel_paystationNightPack1;
    private javax.swing.JLabel jLabel_paystationSmartAccNum;
    private javax.swing.JLabel jLabel_paystationSmartAccNum1;
    private javax.swing.JLabel jLabel_paystationSmartCard;
    private javax.swing.JLabel jLabel_paystationSmartCard1;
    private javax.swing.JLabel jLabel_paystationSmartCardBalance;
    private javax.swing.JLabel jLabel_paystationSmartCardBalance1;
    private javax.swing.JLabel jLabel_paystationSmartCardPack;
    private javax.swing.JLabel jLabel_paystationSmartCardPack1;
    private javax.swing.JLabel jLable_newSmartCard;
    private javax.swing.JLabel jLable_smartCardTopUp;
    private javax.swing.JPanel jPanel_selectSmartCradOptions;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField_paystationCashAmount;
    private javax.swing.JLabel passivceCardNormalCheck;
    private javax.swing.JComboBox passiveCardDestination;
    private javax.swing.JLabel passiveCardNormalNext;
    private javax.swing.JPanel passiveCardPaid;
    private javax.swing.JComboBox passiveCardSourse;
    private javax.swing.JLabel passiveCardTotal;
    private javax.swing.JPanel passivePackages;
    private javax.swing.JLabel paystaionCardId;
    private javax.swing.JLabel paystaionCardId1;
    private javax.swing.JLabel paystaionCardId2;
    private javax.swing.JLabel paystaionCardId3;
    private javax.swing.JLabel paystaionCardId4;
    private javax.swing.JLabel paystaionCardIdEnter;
    private javax.swing.JLabel paystaionCardIdEnterText;
    private javax.swing.JLabel paystaionCardIdEnterText1;
    private javax.swing.JTextField paystaionCardIdInput;
    private javax.swing.JLabel paystaionCardIdInvalid;
    private javax.swing.JTextField paystaionCardPinInput1;
    private javax.swing.JTextField paystaionCardPinInput2;
    private javax.swing.JLabel paystaionCardPinNoMatch;
    private javax.swing.JLabel paystaionCardPinSubmit;
    private javax.swing.JLabel paystaionCardPinTopUp;
    private javax.swing.JTextField paystaionCardPinTopUp1;
    private javax.swing.JLabel paystationInvalidAmount;
    private javax.swing.JPanel paystationPassiveCardNormal;
    private javax.swing.JPanel select_package;
    // End of variables declaration//GEN-END:variables
}
