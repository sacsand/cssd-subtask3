/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import controllers.AccountControl;
import controllers.PackageControl;
import controllers.TravelCardControl;
import entities.DistanceChart;
import entities.Packages;
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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author sachintha
 */
public class PayStationUi extends javax.swing.JFrame {

    private static SmartAccount currentSmartAcc = null;
    private static Packages currentPackage = null;
    private static SmartCard currentSmartCard = null;
    private static boolean currentPassiveCard = false;
    private static boolean topUpAccount = false;
    private static double passiveChargeAmount = 0;
    private static AccountControl theAccounts = AccountControl.getInstance();
    private static TravelCardControl theTravelCards = TravelCardControl.getInstance();
    private static PackageControl thePackages = PackageControl.getInstance();
    private static DistanceChart theDistanceChart = DistanceChart.getInstance(); //cheetah
    private static TempAccount currentTempAccount = null;
    Vector<Packages> p = null;

    File smartAccountFile = new File("smartAccounts.ser");
    File smartCardFile = new File("smartCards.ser");
    File tempAccFile = new File("tempAccounts.ser");//cheetah
    File passiveCardFile = new File("passiveCards.ser");//cheetah
    File packageFile = new File("packages.ser");//cheetah

    /**
     * Creates new form atm
     */
    public PayStationUi() {
        initComponents();

        paystaionCardIdInvalid.setVisible(false); //hide invalid card id error lable
        paystaionCardPinNoMatch.setVisible(false);//hide pin no match error lable
        //paystationInvalidAmount.setVisible(false);//hide invalid amount error lable
        passiveCardNormalNext.setVisible(false);//hide passive card normal next
        cardPayError.setVisible(false); //hide card pay error lable
        paystationPassiveNormalError.setVisible(false); //hide passive normal error lable

        loadAllLocations();

        try {
            thePackages.deserialize();
        } catch (Exception e) {
        }

        p = thePackages.getAllPackages();

        //smart card
        Page_Title34.setText(p.get(0).getPackageName());
        Page_Title35.setText(p.get(1).getPackageName());
        Page_Title36.setText(p.get(2).getPackageName());
        Page_Title39.setText(p.get(3).getPackageName());

        jLabel52OffpeakAmount.setText(String.valueOf(p.get(0).getOffPeakCharge()));
        jLabel52OffpeakAmount1.setText(String.valueOf(p.get(1).getOffPeakCharge()));
        jLabel52OffpeakAmount2.setText(String.valueOf(p.get(2).getOffPeakCharge()));
        jLabel52OffpeakAmount3.setText(String.valueOf(p.get(3).getOffPeakCharge()));

        jLabel59PeakAmount.setText(String.valueOf(p.get(0).getPeakCharge()));
        jLabel59PeakAmount1.setText(String.valueOf(p.get(1).getPeakCharge()));
        jLabel59PeakAmount2.setText(String.valueOf(p.get(2).getPeakCharge()));
        jLabel59PeakAmount3.setText(String.valueOf(p.get(3).getPeakCharge()));

        jLabel88AmountNeed.setText(String.valueOf(p.get(0).getAmountNeeded()));
        jLabel88AmountNeed1.setText(String.valueOf(p.get(1).getAmountNeeded()));
        jLabel88AmountNeed2.setText(String.valueOf(p.get(2).getAmountNeeded()));
        jLabel88AmountNeed3.setText(String.valueOf(p.get(3).getAmountNeeded()));

        //passive card
        Page_Title54.setText(p.get(0).getPackageName());
        Page_Title56.setText(p.get(1).getPackageName());
        Page_Title59.setText(p.get(2).getPackageName());
        Page_Title55.setText(p.get(3).getPackageName());

        jLabel31OffpeakAmount.setText(String.valueOf(p.get(0).getOffPeakCharge()));
        jLabel31OffpeakAmount1.setText(String.valueOf(p.get(1).getOffPeakCharge()));
        jLabel31OffpeakAmount2.setText(String.valueOf(p.get(2).getOffPeakCharge()));
        jLabel31OffpeakAmount3.setText(String.valueOf(p.get(3).getOffPeakCharge()));

        jLabel46PeakAmount.setText(String.valueOf(p.get(0).getPeakCharge()));
        jLabel46PeakAmount1.setText(String.valueOf(p.get(1).getPeakCharge()));
        jLabel46PeakAmount2.setText(String.valueOf(p.get(2).getPeakCharge()));
        jLabel46PeakAmount3.setText(String.valueOf(p.get(3).getPeakCharge()));

        jLabel46PeakAmountNeed.setText(String.valueOf(p.get(0).getAmountNeeded()));
        jLabel46PeakAmountNeed1.setText(String.valueOf(p.get(1).getAmountNeeded()));
        jLabel46PeakAmountNeed2.setText(String.valueOf(p.get(2).getAmountNeeded()));
        jLabel46PeakAmountNeed3.setText(String.valueOf(p.get(3).getAmountNeeded()));

        System.out.println(p);
    }

    public void issueCard(int pin) throws ParseException, IOException, ClassNotFoundException {
        //get current date
        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        String dateString = formatter.format(new Date());
        Date startDate = formatter.parse(dateString);

        if (currentPassiveCard == false) //if it's a smart card
        {

            if (smartAccountFile.exists()) { //if the file exisits 
                theAccounts.deserialize(); //deserialize accounts

            }
            if (smartCardFile.exists()) { //if the file exisits 

                theTravelCards.deserialize(); //deserialize cards
            }

            //create a smart account
            SmartAccount smartAcc = new SmartAccount(startDate, startDate);
            theAccounts.createSmartAccount(smartAcc);

            //create a smartcard
            SmartCard smartCard = new SmartCard(startDate, pin, smartAcc.getAccountNumber(), false);
            theTravelCards.addSmartCard(smartCard);

            //set smart acc's cardid from smartcard
            smartAcc.setCardId(smartCard.getCardId());

            currentSmartAcc = smartAcc; //set current smart acount
            currentSmartCard = smartCard;//set current smart card

        } else { //if it's a passive card

            try {

                //setting a value to the distance chart
                if (tempAccFile.exists()) {
                    theAccounts.deserialize();
                }
                if (passiveCardFile.exists()) {
                    theTravelCards.deserialize();
                }

                //create a temp account
                currentTempAccount = new TempAccount(startDate, startDate, currentPackage.getPackageId());
                theAccounts.createTempeAccount(currentTempAccount);
                System.out.println("currentpack  in issuecard:" + currentPackage);

                boolean direction = theDistanceChart.getDirection((String) passiveCardSourse.getSelectedItem(), (String) passiveCardDestination.getSelectedItem());
                //create a passive card
                PassiveCard passivecard = new PassiveCard(false, (String) passiveCardDestination.getSelectedItem(), startDate, currentTempAccount.getAccountNumber());
                TravelCardControl.getInstance().addPassiveCard(passivecard);
                //set card id to the temp account
                currentTempAccount.setCardId(passivecard.getCardId());
                System.out.println("temp : " + currentTempAccount.getAccountNumber() + " and passive : " + passivecard.getCardId());
                //serialize 
                theAccounts.serialize();
                theTravelCards.serialize();
            } catch (IOException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void topUpAccount(double amount, String accNo) {

        currentSmartAcc = theAccounts.findSmartAccountByAccountNumber(accNo); //find the smart account by acc No  
        currentSmartAcc.topUp(amount); //topup the smart account
        try {
            //serialize new details
            theAccounts.serialize();
            theTravelCards.serialize();
        } catch (IOException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void selectPackage(Packages currentPackage) {
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

        if (currentPassiveCard == false) { //if it's a smart card

            if (currentSmartAcc != null) { //new smart card

                currentSmartAcc.setPackageType(currentPackage.getPackageId());
                topUpAccount(amount, currentSmartAcc.getAccountNumber()); //topup the account

                jLabel_paystationSmartAccNum1.setText(currentSmartAcc.getAccountNumber());
                jLabel_paystationSmartCard1.setText(currentSmartAcc.getCardId());
                jLabel_paystationSmartCardBalance1.setText(String.valueOf(currentSmartAcc.getAmount()));

                jLabel_paystationSmartCardPack1.setText(currentPackage.getPackageName());

                try {
                    theAccounts.serialize();
                } catch (IOException ex) {
                    Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (currentSmartCard != null) { //top-up an exsisting smart card
                try {
                    theAccounts.deserialize();//deserialize accounts
                } catch (IOException ex) {
                    Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
                }

                topUpAccount(amount, currentSmartCard.getAcountNumber()); //topup the account

                currentSmartAcc = theAccounts.findSmartAccountByAccountNumber(currentSmartCard.getAcountNumber());//get the smart account by acc number
                jLabel_paystationSmartAccNum1.setText(currentSmartCard.getAcountNumber());
                jLabel_paystationSmartCard1.setText(currentSmartAcc.getCardId());
                jLabel_paystationSmartCardBalance1.setText(String.valueOf(currentSmartAcc.getAmount()));
                String packageName = thePackages.findPackageByPackageID(currentSmartAcc.getPackageType()).getPackageName();
                jLabel_paystationSmartCardPack1.setText(packageName);

            }

            // removing pane
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(card_paid);
            atm_outer.repaint();
            atm_outer.revalidate();

        } else { //if it's a passive card

            // passiveChargeAmount;
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
        //initialize 
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentTempAccount = null;
        currentPassiveCard = false;
        topUpAccount = false;
        passiveCardTotal.setText("");

    }

    public void validatePin(int pin, int rePin) {

        if (pin == rePin) { //pin number match

            try {
                issueCard(pin); //issue card

                // removing pane
                atm_outer.removeAll();
                atm_outer.repaint();
                atm_outer.revalidate();
                //adding pane
                atm_outer.add(select_package);
                atm_outer.repaint();
                atm_outer.revalidate();

                paystaionCardPinNoMatch.setVisible(false); //hide error message

            } catch (ParseException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            //show error message
            paystaionCardPinNoMatch.setVisible(true);
            paystaionCardPinNoMatch.setText("PIN numbers doesn't match");
        }
    }

    public double calculateCharge(double distance) {

        double result = 0;
        try {
            System.out.println("calculate package :" + currentPackage);
            if (packageFile.exists()) {
                try {
                    PackageControl.deserialize(); //check this again
                } catch (IOException ex) {
                    Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            //currentPackage=currentTempAccount.getPackageType();
            System.out.println("package type :" + currentPackage);
            Packages packType = thePackages.findPackageByPackageID(currentPackage.getPackageId());
            System.out.println("peak :" + packType.getPeakCharge());

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            Date currentTime = sdf.parse(sdf.format(cal.getTime()));
            Date twelvePM = sdf.parse("12:00:00");
            Date eightAM = sdf.parse("08:00:00");
            System.out.println(sdf.format(cal.getTime()));

            if (currentTime.after(eightAM) && currentTime.before(twelvePM)) { //peaktime
                result = distance * packType.getPeakCharge();
                paystationNormalOffpeakOutput.setText(String.valueOf(packType.getPeakCharge()));
            } else { //off peacktime
                result = distance * packType.getOffPeakCharge();
                paystationNormalOffpeakOutput.setText(String.valueOf(packType.getOffPeakCharge()));
            }
            System.out.println("result is for distance : " + result);

        } catch (ParseException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public void displayCharge() {

        double distanceValue = 10;//anuradha
        double rate = 10;//isuru

        passiveCardTotal.setText(String.valueOf(calculateCharge(distanceValue)));

    }

    //cheetah
    public void loadAllPackages() {

        for (Packages pack : thePackages.getAllPackages()) {

        }
    }

    public void loadAllLocations() {
        String locations[] = theDistanceChart.getAllLocations();
        for (int i = 0; i < locations.length; i++) {

            passiveCardSourse.addItem(locations[i]);
            passiveCardDestination.addItem(locations[i]);

        }
    }

    public void selectLocation() {

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
        jCheckBox1 = new javax.swing.JCheckBox();
        atm_outer = new javax.swing.JPanel();
        atm_home = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        Page_Title5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Page_Title7 = new javax.swing.JLabel();
        Page_Title71 = new javax.swing.JLabel();
        Page_Title6 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        Page_Title42 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        cash_pay3 = new javax.swing.JPanel();
        Page_Title52 = new javax.swing.JLabel();
        jLabel_paystaionCashPayHome3 = new javax.swing.JLabel();
        jLabel_paystaionCashPayBack1 = new javax.swing.JLabel();
        jTextField_paystationCashAmount = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        Page_Title53 = new javax.swing.JLabel();
        blue40 = new javax.swing.JLabel();
        blue45 = new javax.swing.JLabel();
        blue46 = new javax.swing.JLabel();
        blue47 = new javax.swing.JLabel();
        gray40 = new javax.swing.JLabel();
        gray45 = new javax.swing.JLabel();
        gray46 = new javax.swing.JLabel();
        gray47 = new javax.swing.JLabel();
        paystationInvalidAmount = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        Page_Title61 = new javax.swing.JLabel();
        Page_Title62 = new javax.swing.JLabel();
        Page_Title63 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        atm_payment_selct = new javax.swing.JPanel();
        paystationPayByCash = new javax.swing.JLabel();
        paystationPayByCard = new javax.swing.JLabel();
        blue16 = new javax.swing.JLabel();
        blue17 = new javax.swing.JLabel();
        blue18 = new javax.swing.JLabel();
        gray16 = new javax.swing.JLabel();
        gray17 = new javax.swing.JLabel();
        gray18 = new javax.swing.JLabel();
        gray19 = new javax.swing.JLabel();
        blue19 = new javax.swing.JLabel();
        jLabel_paystaionPaymentHome = new javax.swing.JLabel();
        jLabel_paystaionPaymentBack = new javax.swing.JLabel();
        Page_Title8 = new javax.swing.JLabel();
        Page_Title9 = new javax.swing.JLabel();
        Page_Title10 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        Page_Title32 = new javax.swing.JLabel();
        jLabel_payStationBack = new javax.swing.JLabel();
        paystationPayByCard1 = new javax.swing.JLabel();
        paystationPayByCash1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        card_selet = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel_paystaionCardSelectHome = new javax.swing.JLabel();
        jLabel_paystaionCardSelectBack = new javax.swing.JLabel();
        Page_Title11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        blue21 = new javax.swing.JLabel();
        blue22 = new javax.swing.JLabel();
        blue23 = new javax.swing.JLabel();
        blue24 = new javax.swing.JLabel();
        gray21 = new javax.swing.JLabel();
        gray22 = new javax.swing.JLabel();
        gray23 = new javax.swing.JLabel();
        gray24 = new javax.swing.JLabel();
        gray25 = new javax.swing.JLabel();
        blue25 = new javax.swing.JLabel();
        Page_Title12 = new javax.swing.JLabel();
        Page_Title14 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        Page_Title13 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        Page_Title64 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        card_pay = new javax.swing.JPanel();
        Page_Title15 = new javax.swing.JLabel();
        jLabel65CardPayAmount = new javax.swing.JLabel();
        Page_Title41 = new javax.swing.JLabel();
        cardPayCardNo = new javax.swing.JTextField();
        cardPayError = new javax.swing.JLabel();
        blue26 = new javax.swing.JLabel();
        blue27 = new javax.swing.JLabel();
        blue28 = new javax.swing.JLabel();
        blue29 = new javax.swing.JLabel();
        blue30 = new javax.swing.JLabel();
        gray26 = new javax.swing.JLabel();
        gray27 = new javax.swing.JLabel();
        gray28 = new javax.swing.JLabel();
        gray29 = new javax.swing.JLabel();
        gray30 = new javax.swing.JLabel();
        jLabel_paystaionCardPayHome = new javax.swing.JLabel();
        jLabel_paystaionCardPayBack = new javax.swing.JLabel();
        paystationCardPayPin = new javax.swing.JTextField();
        Page_Title16 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        Page_Title19 = new javax.swing.JLabel();
        Page_Title17 = new javax.swing.JLabel();
        Page_Title33 = new javax.swing.JLabel();
        Page_Title18 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        card_paid = new javax.swing.JPanel();
        Page_Title20 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel_paystationSmartAccNum = new javax.swing.JLabel();
        jLabel_paystationSmartAccNum1 = new javax.swing.JLabel();
        jLabel_paystationSmartCard = new javax.swing.JLabel();
        jLabel_paystationSmartCard1 = new javax.swing.JLabel();
        jLabel_paystationSmartCardPack = new javax.swing.JLabel();
        jLabel_paystationSmartCardPack1 = new javax.swing.JLabel();
        jLabel_paystationSmartCardBalance = new javax.swing.JLabel();
        jLabel_paystationSmartCardBalance1 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Page_Title23 = new javax.swing.JLabel();
        Page_Title24 = new javax.swing.JLabel();
        Page_Title25 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        cash_pay = new javax.swing.JPanel();
        Page_Title21 = new javax.swing.JLabel();
        jLabel_paystaionCashPayHome = new javax.swing.JLabel();
        blue41 = new javax.swing.JLabel();
        blue42 = new javax.swing.JLabel();
        blue43 = new javax.swing.JLabel();
        blue44 = new javax.swing.JLabel();
        gray41 = new javax.swing.JLabel();
        gray42 = new javax.swing.JLabel();
        gray43 = new javax.swing.JLabel();
        gray44 = new javax.swing.JLabel();
        jLabel_paystaionCashPayBack = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        Page_Title26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        Page_Title27 = new javax.swing.JLabel();
        Page_Title28 = new javax.swing.JLabel();
        Page_Title29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        select_package = new javax.swing.JPanel();
        Page_Title39 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel52OffpeakAmount = new javax.swing.JLabel();
        jLabel59PeakAmount = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel88AmountNeed = new javax.swing.JLabel();
        jLabel88AmountNeed1 = new javax.swing.JLabel();
        jLabel88AmountNeed2 = new javax.swing.JLabel();
        jLabel88AmountNeed3 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel52OffpeakAmount1 = new javax.swing.JLabel();
        jLabel59PeakAmount1 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel52OffpeakAmount2 = new javax.swing.JLabel();
        jLabel59PeakAmount2 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        blue11 = new javax.swing.JLabel();
        blue12 = new javax.swing.JLabel();
        gray11 = new javax.swing.JLabel();
        gray12 = new javax.swing.JLabel();
        gray13 = new javax.swing.JLabel();
        gray14 = new javax.swing.JLabel();
        blue13 = new javax.swing.JLabel();
        blue14 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel52OffpeakAmount3 = new javax.swing.JLabel();
        jLabel59PeakAmount3 = new javax.swing.JLabel();
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
        Page_Title65 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jLabel_paystationNightPack4 = new javax.swing.JLabel();
        jLabel_paystationDayPack2 = new javax.swing.JLabel();
        jLabel_paystationBudjectPack1 = new javax.swing.JLabel();
        jLabel_paystationMegaPack1 = new javax.swing.JLabel();
        jPanel_selectSmartCradOptions = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel_paystaionSmartCardOptionHome = new javax.swing.JLabel();
        jLabel_paystaionSmartCardOptionBack = new javax.swing.JLabel();
        Page_Title40 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLable_newSmartCard = new javax.swing.JLabel();
        jLable_smartCardTopUp = new javax.swing.JLabel();
        Page_Title43 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        enterCardId = new javax.swing.JPanel();
        paystaionCardIdInvalid = new javax.swing.JLabel();
        paystaionCardIdEnter = new javax.swing.JLabel();
        blue1 = new javax.swing.JLabel();
        gray1 = new javax.swing.JLabel();
        gray2 = new javax.swing.JLabel();
        gray3 = new javax.swing.JLabel();
        gray4 = new javax.swing.JLabel();
        gray5 = new javax.swing.JLabel();
        blue2 = new javax.swing.JLabel();
        blue3 = new javax.swing.JLabel();
        blue4 = new javax.swing.JLabel();
        blue5 = new javax.swing.JLabel();
        paystaionCardId2 = new javax.swing.JLabel();
        jLabel_paystaionCardPayHome1 = new javax.swing.JLabel();
        jLabel_paystaionCardPayBack1 = new javax.swing.JLabel();
        paystaionCardIdInput = new javax.swing.JTextField();
        paystaionCardPinTopUp = new javax.swing.JLabel();
        paystaionCardPinTopUp1 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        paystaionCardId = new javax.swing.JLabel();
        Page_Title47 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        paystaionCardPinTopUp2 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        enterCardPin = new javax.swing.JPanel();
        paystaionCardPinNoMatch = new javax.swing.JLabel();
        paystaionCardId3 = new javax.swing.JLabel();
        blue6 = new javax.swing.JLabel();
        gray6 = new javax.swing.JLabel();
        gray7 = new javax.swing.JLabel();
        gray8 = new javax.swing.JLabel();
        gray9 = new javax.swing.JLabel();
        blue7 = new javax.swing.JLabel();
        blue8 = new javax.swing.JLabel();
        blue9 = new javax.swing.JLabel();
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
        jLabel45 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        paystaionCardId5 = new javax.swing.JLabel();
        paystationPassiveCardNormal = new javax.swing.JPanel();
        paystationNormalOffpeak = new javax.swing.JLabel();
        paystationNormalOffpeakOutput = new javax.swing.JLabel();
        paystationNormalPeakOutput = new javax.swing.JLabel();
        blue36 = new javax.swing.JLabel();
        blue37 = new javax.swing.JLabel();
        gray36 = new javax.swing.JLabel();
        gray37 = new javax.swing.JLabel();
        gray38 = new javax.swing.JLabel();
        gray39 = new javax.swing.JLabel();
        blue38 = new javax.swing.JLabel();
        blue39 = new javax.swing.JLabel();
        paystationNormalPeak = new javax.swing.JLabel();
        passiveCardNormalNext = new javax.swing.JLabel();
        paystationPassiveNormalError = new javax.swing.JLabel();
        passivceCardNormalCheck = new javax.swing.JLabel();
        passiveCardTotal = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        passiveCardSourse = new javax.swing.JComboBox();
        passiveCardDestination = new javax.swing.JComboBox();
        jLabel_paystaionPaymentHome1 = new javax.swing.JLabel();
        jLabel_paystaionPaymentBack1 = new javax.swing.JLabel();
        Page_Title44 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        Page_Title45 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        Hariya = new javax.swing.JLabel();
        journeyAmount1 = new javax.swing.JLabel();
        journeyAmount2 = new javax.swing.JLabel();
        jLabel115 = new javax.swing.JLabel();
        passiveCardPaid = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        Page_Title49 = new javax.swing.JLabel();
        Page_Title50 = new javax.swing.JLabel();
        Page_Title51 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        passivePackages = new javax.swing.JPanel();
        jLabel_paystaionPackageHome1 = new javax.swing.JLabel();
        jLabel46Peak4 = new javax.swing.JLabel();
        jLabel46PeakAmountNeed = new javax.swing.JLabel();
        jLabel46Peak5 = new javax.swing.JLabel();
        jLabel46PeakAmountNeed1 = new javax.swing.JLabel();
        jLabel46Peak6 = new javax.swing.JLabel();
        jLabel46PeakAmountNeed2 = new javax.swing.JLabel();
        jLabel46Peak7 = new javax.swing.JLabel();
        jLabel46PeakAmountNeed3 = new javax.swing.JLabel();
        jLabel31Offpeak2 = new javax.swing.JLabel();
        jLabel46Peak2 = new javax.swing.JLabel();
        jLabel31OffpeakAmount2 = new javax.swing.JLabel();
        jLabel46PeakAmount2 = new javax.swing.JLabel();
        jLabel31Offpeak1 = new javax.swing.JLabel();
        jLabel46Peak1 = new javax.swing.JLabel();
        jLabel31OffpeakAmount1 = new javax.swing.JLabel();
        jLabel46PeakAmount1 = new javax.swing.JLabel();
        jLabel46Peak = new javax.swing.JLabel();
        jLabel46PeakAmount = new javax.swing.JLabel();
        blue31 = new javax.swing.JLabel();
        gray31 = new javax.swing.JLabel();
        gray32 = new javax.swing.JLabel();
        gray33 = new javax.swing.JLabel();
        gray34 = new javax.swing.JLabel();
        gray35 = new javax.swing.JLabel();
        blue32 = new javax.swing.JLabel();
        blue33 = new javax.swing.JLabel();
        blue34 = new javax.swing.JLabel();
        blue35 = new javax.swing.JLabel();
        jLabel31Offpeak3 = new javax.swing.JLabel();
        jLabel46Peak3 = new javax.swing.JLabel();
        jLabel31OffpeakAmount3 = new javax.swing.JLabel();
        jLabel46PeakAmount3 = new javax.swing.JLabel();
        jLabel31OffpeakAmount = new javax.swing.JLabel();
        jLabel31Offpeak = new javax.swing.JLabel();
        jLabel_paystaionPackageBack1 = new javax.swing.JLabel();
        Page_Title55 = new javax.swing.JLabel();
        Page_Title59 = new javax.swing.JLabel();
        jLabel_paystationNightPack3 = new javax.swing.JLabel();
        jLabel_paystationNightPack2 = new javax.swing.JLabel();
        Page_Title54 = new javax.swing.JLabel();
        jLabel_paystationNightPack1 = new javax.swing.JLabel();
        Page_Title56 = new javax.swing.JLabel();
        Page_Title57 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel_paystationDayPack1 = new javax.swing.JLabel();
        Page_Title58 = new javax.swing.JLabel();
        jLabel112 = new javax.swing.JLabel();
        jLabel_paystationNightPack5 = new javax.swing.JLabel();
        jLabel_paystationDayPack3 = new javax.swing.JLabel();
        jLabel_paystationNightPack6 = new javax.swing.JLabel();
        jLabel_paystationNightPack7 = new javax.swing.JLabel();
        passiveCardPayment = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        passiveCardPayAmount = new javax.swing.JLabel();
        jLabel_paystaionPackageHome2 = new javax.swing.JLabel();
        jLabel_paystaionPackageBack2 = new javax.swing.JLabel();
        Page_Title60 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        Faq1 = new javax.swing.JPanel();
        Page_Title67 = new javax.swing.JLabel();
        jLabel_paystaionPackageHome3 = new javax.swing.JLabel();
        Page_Title69 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        Page_Title70 = new javax.swing.JLabel();
        Page_Title68 = new javax.swing.JLabel();
        Page_Title72 = new javax.swing.JLabel();
        Page_Title73 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        Page_Title74 = new javax.swing.JLabel();
        Page_Title75 = new javax.swing.JLabel();
        Page_Title76 = new javax.swing.JLabel();
        Page_Title77 = new javax.swing.JLabel();
        Page_Title89 = new javax.swing.JLabel();
        Faq2 = new javax.swing.JPanel();
        Page_Title78 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        Page_Title79 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        Page_Title80 = new javax.swing.JLabel();
        Page_Title81 = new javax.swing.JLabel();
        Page_Title82 = new javax.swing.JLabel();
        Page_Title83 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        Page_Title84 = new javax.swing.JLabel();
        Page_Title85 = new javax.swing.JLabel();
        Page_Title86 = new javax.swing.JLabel();
        Page_Title87 = new javax.swing.JLabel();
        Page_Title88 = new javax.swing.JLabel();
        card_pay1 = new javax.swing.JPanel();
        Page_Title90 = new javax.swing.JLabel();
        jLabel65CardPayAmount1 = new javax.swing.JLabel();
        blue48 = new javax.swing.JLabel();
        blue49 = new javax.swing.JLabel();
        blue50 = new javax.swing.JLabel();
        blue51 = new javax.swing.JLabel();
        blue52 = new javax.swing.JLabel();
        gray48 = new javax.swing.JLabel();
        gray49 = new javax.swing.JLabel();
        gray50 = new javax.swing.JLabel();
        gray51 = new javax.swing.JLabel();
        gray52 = new javax.swing.JLabel();
        Page_Title91 = new javax.swing.JLabel();
        cardPayCardNo1 = new javax.swing.JTextField();
        cardPayError1 = new javax.swing.JLabel();
        jLabel_paystaionCardPayHome3 = new javax.swing.JLabel();
        jLabel_paystaionCardPayBack3 = new javax.swing.JLabel();
        paystationCardPayPin1 = new javax.swing.JTextField();
        paystationCardAmount12 = new javax.swing.JTextField();
        Page_Title97CardAmount = new javax.swing.JLabel();
        Page_Title92 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        Page_Title93 = new javax.swing.JLabel();
        Page_Title94 = new javax.swing.JLabel();
        Page_Title96 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/AMf9X7E.jpg"))); // NOI18N

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(775, 515));
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(null);

        atm_outer.setLayout(new java.awt.CardLayout());

        atm_home.setBackground(new java.awt.Color(34, 47, 47));
        atm_home.setForeground(new java.awt.Color(0, 102, 102));
        atm_home.setLayout(null);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel2.setText("Add");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        atm_home.add(jLabel2);
        jLabel2.setBounds(420, 200, 290, 200);

        Page_Title5.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title5.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title5.setForeground(java.awt.Color.white);
        Page_Title5.setText("Get Tickets");
        atm_home.add(Page_Title5);
        Page_Title5.setBounds(510, 360, 170, 40);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel3.setText("Add");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        atm_home.add(jLabel3);
        jLabel3.setBounds(70, 200, 290, 200);

        Page_Title7.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title7.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title7.setForeground(java.awt.Color.white);
        Page_Title7.setText("Get Smart Card");
        atm_home.add(Page_Title7);
        Page_Title7.setBounds(130, 360, 170, 40);

        Page_Title71.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title71.setFont(new java.awt.Font("Droid Sans", 0, 24)); // NOI18N
        Page_Title71.setForeground(java.awt.Color.white);
        Page_Title71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/icon-36-ldpi.png"))); // NOI18N
        Page_Title71.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Page_Title71MouseClicked(evt);
            }
        });
        atm_home.add(Page_Title71);
        Page_Title71.setBounds(720, 10, 50, 50);

        Page_Title6.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title6.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title6.setForeground(java.awt.Color.white);
        Page_Title6.setText("Lydia");
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
        jLabel26.setBounds(0, 0, 780, 80);

        Page_Title42.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title42.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        Page_Title42.setForeground(java.awt.Color.white);
        Page_Title42.setText("Select An Option");
        atm_home.add(Page_Title42);
        Page_Title42.setBounds(50, 90, 530, 60);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-76@2x.png"))); // NOI18N
        atm_home.add(jLabel6);
        jLabel6.setBounds(490, 210, 152, 150);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-72@2x.png"))); // NOI18N
        atm_home.add(jLabel9);
        jLabel9.setBounds(140, 210, 200, 150);

        jLabel73.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel73.setText("Add");
        jLabel73.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel73MouseClicked(evt);
            }
        });
        atm_home.add(jLabel73);
        jLabel73.setBounds(70, 360, 290, 40);

        jLabel74.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel74.setText("Add");
        jLabel74.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel74MouseClicked(evt);
            }
        });
        atm_home.add(jLabel74);
        jLabel74.setBounds(420, 360, 290, 40);

        jLabel33.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(153, 153, 153));
        jLabel33.setText("Home/Card/");
        atm_home.add(jLabel33);
        jLabel33.setBounds(10, 480, 90, 17);

        atm_outer.add(atm_home, "card2");

        cash_pay3.setBackground(new java.awt.Color(36, 43, 43));
        cash_pay3.setLayout(null);

        Page_Title52.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title52.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title52.setForeground(java.awt.Color.white);
        Page_Title52.setText("Enter The Amount");
        cash_pay3.add(Page_Title52);
        Page_Title52.setBounds(290, 140, 240, 30);

        jLabel_paystaionCashPayHome3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionCashPayHome3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCashPayHome3MouseClicked(evt);
            }
        });
        cash_pay3.add(jLabel_paystaionCashPayHome3);
        jLabel_paystaionCashPayHome3.setBounds(720, 10, 30, 30);

        jLabel_paystaionCashPayBack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCashPayBack1.setText("BACK");
        jLabel_paystaionCashPayBack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCashPayBack1MouseClicked(evt);
            }
        });
        cash_pay3.add(jLabel_paystaionCashPayBack1);
        jLabel_paystaionCashPayBack1.setBounds(680, 10, 30, 30);

        jTextField_paystationCashAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_paystationCashAmountActionPerformed(evt);
            }
        });
        cash_pay3.add(jTextField_paystationCashAmount);
        jTextField_paystationCashAmount.setBounds(280, 200, 220, 40);

        jLabel65.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/callout-red-bg (2).png"))); // NOI18N
        jLabel65.setText("Add");
        jLabel65.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel65MouseClicked(evt);
            }
        });
        cash_pay3.add(jLabel65);
        jLabel65.setBounds(430, 350, 290, 60);

        Page_Title53.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title53.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title53.setForeground(java.awt.Color.white);
        Page_Title53.setText("Cancel The Payment");
        Page_Title53.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Page_Title53MouseClicked(evt);
            }
        });
        cash_pay3.add(Page_Title53);
        Page_Title53.setBounds(500, 360, 240, 40);

        blue40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        cash_pay3.add(blue40);
        blue40.setBounds(140, 620, 60, 25);

        blue45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        cash_pay3.add(blue45);
        blue45.setBounds(200, 620, 60, 25);

        blue46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        cash_pay3.add(blue46);
        blue46.setBounds(250, 620, 60, 25);

        blue47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue4.png"))); // NOI18N
        cash_pay3.add(blue47);
        blue47.setBounds(310, 620, 60, 25);

        gray40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        cash_pay3.add(gray40);
        gray40.setBounds(140, 620, 60, 25);

        gray45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        cash_pay3.add(gray45);
        gray45.setBounds(200, 620, 60, 25);

        gray46.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        cash_pay3.add(gray46);
        gray46.setBounds(250, 620, 60, 25);

        gray47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        cash_pay3.add(gray47);
        gray47.setBounds(310, 620, 60, 25);

        paystationInvalidAmount.setBackground(new java.awt.Color(102, 102, 102));
        paystationInvalidAmount.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        paystationInvalidAmount.setForeground(java.awt.Color.white);
        paystationInvalidAmount.setText("Invalid amount. Re-Enter");
        cash_pay3.add(paystationInvalidAmount);
        paystationInvalidAmount.setBounds(260, 260, 290, 30);

        jLabel66.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel66.setText("Add");
        jLabel66.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel66MouseClicked(evt);
            }
        });
        cash_pay3.add(jLabel66);
        jLabel66.setBounds(0, 0, 780, 80);
        cash_pay3.add(jLabel67);
        jLabel67.setBounds(290, 120, 200, 30);

        jLabel68.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel68.setText("Add");
        jLabel68.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel68MouseClicked(evt);
            }
        });
        cash_pay3.add(jLabel68);
        jLabel68.setBounds(130, 350, 290, 60);

        Page_Title61.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title61.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title61.setForeground(java.awt.Color.white);
        Page_Title61.setText("Accept The Payment");
        cash_pay3.add(Page_Title61);
        Page_Title61.setBounds(190, 360, 220, 40);

        Page_Title62.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title62.setFont(new java.awt.Font("Ubuntu Light", 0, 28)); // NOI18N
        Page_Title62.setForeground(java.awt.Color.white);
        Page_Title62.setText("Pay By Cash");
        cash_pay3.add(Page_Title62);
        Page_Title62.setBounds(40, 100, 410, 30);

        Page_Title63.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title63.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title63.setForeground(java.awt.Color.white);
        Page_Title63.setText("Lydia");
        cash_pay3.add(Page_Title63);
        Page_Title63.setBounds(20, 0, 360, 60);
        cash_pay3.add(jLabel70);
        jLabel70.setBounds(280, 130, 200, 30);

        jLabel75.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue_48.png"))); // NOI18N
        cash_pay3.add(jLabel75);
        jLabel75.setBounds(140, 350, 50, 60);

        jLabel76.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/mdpi.png"))); // NOI18N
        cash_pay3.add(jLabel76);
        jLabel76.setBounds(440, 340, 50, 80);

        atm_outer.add(cash_pay3, "card2");

        atm_payment_selct.setBackground(new java.awt.Color(34, 40, 40));
        atm_payment_selct.setLayout(null);

        paystationPayByCash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        paystationPayByCash.setText("Add");
        paystationPayByCash.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paystationPayByCashMouseClicked(evt);
            }
        });
        atm_payment_selct.add(paystationPayByCash);
        paystationPayByCash.setBounds(140, 320, 460, 80);

        paystationPayByCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        paystationPayByCard.setText("Add");
        paystationPayByCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paystationPayByCardMouseClicked(evt);
            }
        });
        atm_payment_selct.add(paystationPayByCard);
        paystationPayByCard.setBounds(140, 190, 460, 80);

        blue16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        atm_payment_selct.add(blue16);
        blue16.setBounds(20, 570, 60, 25);

        blue17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        atm_payment_selct.add(blue17);
        blue17.setBounds(70, 570, 60, 25);

        blue18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        atm_payment_selct.add(blue18);
        blue18.setBounds(120, 570, 60, 25);

        gray16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        atm_payment_selct.add(gray16);
        gray16.setBounds(20, 570, 60, 25);

        gray17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        atm_payment_selct.add(gray17);
        gray17.setBounds(70, 570, 60, 25);

        gray18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        atm_payment_selct.add(gray18);
        gray18.setBounds(120, 570, 60, 25);

        gray19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        atm_payment_selct.add(gray19);
        gray19.setBounds(180, 570, 60, 25);

        blue19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        atm_payment_selct.add(blue19);
        blue19.setBounds(180, 570, 60, 25);

        jLabel_paystaionPaymentHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionPaymentHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPaymentHomeMouseClicked(evt);
            }
        });
        atm_payment_selct.add(jLabel_paystaionPaymentHome);
        jLabel_paystaionPaymentHome.setBounds(720, 10, 30, 30);

        jLabel_paystaionPaymentBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionPaymentBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPaymentBackMouseClicked(evt);
            }
        });
        atm_payment_selct.add(jLabel_paystaionPaymentBack);
        jLabel_paystaionPaymentBack.setBounds(680, 10, 30, 30);

        Page_Title8.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title8.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title8.setForeground(java.awt.Color.white);
        Page_Title8.setText("Pay by Cash");
        atm_payment_selct.add(Page_Title8);
        Page_Title8.setBounds(310, 320, 170, 80);

        Page_Title9.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title9.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title9.setForeground(java.awt.Color.white);
        Page_Title9.setText("Pay by Card");
        atm_payment_selct.add(Page_Title9);
        Page_Title9.setBounds(300, 190, 170, 80);

        Page_Title10.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title10.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title10.setForeground(java.awt.Color.white);
        Page_Title10.setText("Lydia  ");
        atm_payment_selct.add(Page_Title10);
        Page_Title10.setBounds(10, 0, 260, 60);

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel25MouseClicked(evt);
            }
        });
        atm_payment_selct.add(jLabel25);
        jLabel25.setBounds(0, 0, 780, 80);

        Page_Title32.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title32.setFont(new java.awt.Font("Ubuntu Light", 0, 27)); // NOI18N
        Page_Title32.setForeground(java.awt.Color.white);
        Page_Title32.setText("How Do You Want To Pay");
        atm_payment_selct.add(Page_Title32);
        Page_Title32.setBounds(50, 110, 380, 50);

        jLabel_payStationBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/cardIcon-Smalcal-50.png"))); // NOI18N
        atm_payment_selct.add(jLabel_payStationBack);
        jLabel_payStationBack.setBounds(160, 210, 50, 40);

        paystationPayByCard1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        paystationPayByCard1.setText("Add");
        paystationPayByCard1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paystationPayByCard1MouseClicked(evt);
            }
        });
        atm_payment_selct.add(paystationPayByCard1);
        paystationPayByCard1.setBounds(140, 190, 90, 80);

        paystationPayByCash1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        paystationPayByCash1.setText("Add");
        paystationPayByCash1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paystationPayByCash1MouseClicked(evt);
            }
        });
        atm_payment_selct.add(paystationPayByCash1);
        paystationPayByCash1.setBounds(140, 320, 90, 80);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/Icon-Small-50.png"))); // NOI18N
        atm_payment_selct.add(jLabel14);
        jLabel14.setBounds(160, 330, 60, 70);

        jLabel105.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel105.setForeground(new java.awt.Color(153, 153, 153));
        jLabel105.setText("Home / Card / NewCard / Packages / PaymentType /");
        atm_payment_selct.add(jLabel105);
        jLabel105.setBounds(10, 490, 380, 17);

        atm_outer.add(atm_payment_selct, "card2");

        card_selet.setBackground(new java.awt.Color(34, 40, 38));
        card_selet.setLayout(null);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel10.setText("Add");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });
        card_selet.add(jLabel10);
        jLabel10.setBounds(180, 150, 410, 70);

        jLabel_paystaionCardSelectHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionCardSelectHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardSelectHomeMouseClicked(evt);
            }
        });
        card_selet.add(jLabel_paystaionCardSelectHome);
        jLabel_paystaionCardSelectHome.setBounds(720, 10, 30, 30);

        jLabel_paystaionCardSelectBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCardSelectBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardSelectBackMouseClicked(evt);
            }
        });
        card_selet.add(jLabel_paystaionCardSelectBack);
        jLabel_paystaionCardSelectBack.setBounds(680, 10, 30, 30);

        Page_Title11.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title11.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title11.setForeground(java.awt.Color.white);
        Page_Title11.setText("PayPal");
        card_selet.add(Page_Title11);
        Page_Title11.setBounds(390, 170, 170, 30);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel7.setText("Add");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        card_selet.add(jLabel7);
        jLabel7.setBounds(180, 370, 90, 70);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel8.setText("Add");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        card_selet.add(jLabel8);
        jLabel8.setBounds(180, 260, 410, 70);

        blue21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        card_selet.add(blue21);
        blue21.setBounds(240, 530, 60, 25);

        blue22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        card_selet.add(blue22);
        blue22.setBounds(300, 530, 60, 25);

        blue23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        card_selet.add(blue23);
        blue23.setBounds(350, 530, 60, 25);

        blue24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue4.png"))); // NOI18N
        card_selet.add(blue24);
        blue24.setBounds(410, 530, 60, 25);

        gray21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        card_selet.add(gray21);
        gray21.setBounds(240, 530, 60, 25);

        gray22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        card_selet.add(gray22);
        gray22.setBounds(300, 530, 60, 25);

        gray23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        card_selet.add(gray23);
        gray23.setBounds(350, 530, 60, 25);

        gray24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        card_selet.add(gray24);
        gray24.setBounds(410, 530, 60, 25);

        gray25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray5.png"))); // NOI18N
        card_selet.add(gray25);
        gray25.setBounds(470, 530, 60, 25);

        blue25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray5.png"))); // NOI18N
        card_selet.add(blue25);
        blue25.setBounds(470, 530, 60, 25);

        Page_Title12.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title12.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title12.setForeground(java.awt.Color.white);
        Page_Title12.setText("Visa");
        card_selet.add(Page_Title12);
        Page_Title12.setBounds(400, 280, 70, 30);

        Page_Title14.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title14.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title14.setForeground(java.awt.Color.white);
        Page_Title14.setText("Master Card");
        card_selet.add(Page_Title14);
        Page_Title14.setBounds(360, 390, 170, 30);

        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/interfaces/mhdpi.png"))); // NOI18N
        jLabel40.setText("jLabel40");
        card_selet.add(jLabel40);
        jLabel40.setBounds(190, 150, 70, 70);

        Page_Title13.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title13.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title13.setForeground(java.awt.Color.white);
        Page_Title13.setText("Lydia");
        card_selet.add(Page_Title13);
        Page_Title13.setBounds(20, 0, 250, 60);

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel24.setText("Add");
        jLabel24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel24MouseClicked(evt);
            }
        });
        card_selet.add(jLabel24);
        jLabel24.setBounds(0, 0, 800, 80);

        Page_Title64.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title64.setFont(new java.awt.Font("Ubuntu Light", 0, 27)); // NOI18N
        Page_Title64.setForeground(java.awt.Color.white);
        Page_Title64.setText("Please Choose your card ");
        card_selet.add(Page_Title64);
        Page_Title64.setBounds(30, 100, 330, 30);

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel21.setText("Add");
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
        });
        card_selet.add(jLabel21);
        jLabel21.setBounds(180, 150, 90, 70);

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/hdpi.png"))); // NOI18N
        jLabel41.setText("jLabel41");
        card_selet.add(jLabel41);
        jLabel41.setBounds(190, 260, 90, 70);

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel29.setText("Add");
        jLabel29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel29MouseClicked(evt);
            }
        });
        card_selet.add(jLabel29);
        jLabel29.setBounds(180, 260, 90, 70);

        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/phdpi.png"))); // NOI18N
        jLabel44.setText("jLabel44");
        card_selet.add(jLabel44);
        jLabel44.setBounds(190, 370, 90, 70);

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel35.setText("Add");
        jLabel35.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel35MouseClicked(evt);
            }
        });
        card_selet.add(jLabel35);
        jLabel35.setBounds(180, 370, 410, 70);

        jLabel106.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel106.setForeground(new java.awt.Color(153, 153, 153));
        jLabel106.setText("Home / Card / NewCard / Packages / PaymentType / payByCard");
        card_selet.add(jLabel106);
        jLabel106.setBounds(10, 490, 450, 17);

        atm_outer.add(card_selet, "card2");

        card_pay.setBackground(new java.awt.Color(38, 45, 45));
        card_pay.setLayout(null);

        Page_Title15.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title15.setFont(new java.awt.Font("Ubuntu Light", 0, 27)); // NOI18N
        Page_Title15.setForeground(java.awt.Color.white);
        Page_Title15.setText("Pay By Card ");
        card_pay.add(Page_Title15);
        Page_Title15.setBounds(20, 100, 200, 30);
        card_pay.add(jLabel65CardPayAmount);
        jLabel65CardPayAmount.setBounds(40, 210, 130, 40);

        Page_Title41.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title41.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title41.setForeground(java.awt.Color.white);
        Page_Title41.setText("Card No");
        card_pay.add(Page_Title41);
        Page_Title41.setBounds(330, 150, 100, 30);

        cardPayCardNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cardPayCardNoActionPerformed(evt);
            }
        });
        card_pay.add(cardPayCardNo);
        cardPayCardNo.setBounds(280, 190, 210, 40);

        cardPayError.setBackground(new java.awt.Color(255, 51, 0));
        cardPayError.setForeground(new java.awt.Color(255, 255, 255));
        cardPayError.setText("jLabel31");
        card_pay.add(cardPayError);
        cardPayError.setBounds(340, 320, 120, 20);

        blue26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        card_pay.add(blue26);
        blue26.setBounds(10, 530, 60, 25);

        blue27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        card_pay.add(blue27);
        blue27.setBounds(70, 530, 60, 25);

        blue28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        card_pay.add(blue28);
        blue28.setBounds(120, 530, 60, 25);

        blue29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue4.png"))); // NOI18N
        card_pay.add(blue29);
        blue29.setBounds(180, 530, 60, 25);

        blue30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue5.png"))); // NOI18N
        card_pay.add(blue30);
        blue30.setBounds(240, 530, 60, 25);

        gray26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        card_pay.add(gray26);
        gray26.setBounds(10, 530, 60, 25);

        gray27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        card_pay.add(gray27);
        gray27.setBounds(70, 530, 60, 25);

        gray28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        card_pay.add(gray28);
        gray28.setBounds(120, 530, 60, 25);

        gray29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        card_pay.add(gray29);
        gray29.setBounds(180, 530, 60, 25);

        gray30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray5.png"))); // NOI18N
        card_pay.add(gray30);
        gray30.setBounds(240, 530, 60, 25);

        jLabel_paystaionCardPayHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionCardPayHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayHomeMouseClicked(evt);
            }
        });
        card_pay.add(jLabel_paystaionCardPayHome);
        jLabel_paystaionCardPayHome.setBounds(720, 20, 40, 30);

        jLabel_paystaionCardPayBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCardPayBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayBackMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayBackMouseEntered(evt);
            }
        });
        card_pay.add(jLabel_paystaionCardPayBack);
        jLabel_paystaionCardPayBack.setBounds(680, 20, 40, 30);

        paystationCardPayPin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystationCardPayPinActionPerformed(evt);
            }
        });
        card_pay.add(paystationCardPayPin);
        paystationCardPayPin.setBounds(280, 280, 210, 40);

        Page_Title16.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title16.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title16.setForeground(java.awt.Color.white);
        Page_Title16.setText("          Cancel The Payment");
        Page_Title16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Page_Title16MouseClicked(evt);
            }
        });
        card_pay.add(Page_Title16);
        Page_Title16.setBounds(410, 370, 310, 60);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/callout-red-bg (2).png"))); // NOI18N
        jLabel11.setText("Add");
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });
        card_pay.add(jLabel11);
        jLabel11.setBounds(410, 370, 310, 60);

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel23.setText("Add");
        jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel23MouseClicked(evt);
            }
        });
        card_pay.add(jLabel23);
        jLabel23.setBounds(0, 0, 790, 80);
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
        jLabel13.setBounds(90, 370, 300, 60);

        Page_Title19.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title19.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title19.setForeground(java.awt.Color.white);
        Page_Title19.setText("Accept The Payment");
        card_pay.add(Page_Title19);
        Page_Title19.setBounds(160, 380, 230, 30);

        Page_Title17.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title17.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title17.setForeground(java.awt.Color.white);
        Page_Title17.setText("PIN");
        card_pay.add(Page_Title17);
        Page_Title17.setBounds(360, 240, 40, 30);

        Page_Title33.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title33.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title33.setForeground(java.awt.Color.white);
        Page_Title33.setText("Total Amount");
        card_pay.add(Page_Title33);
        Page_Title33.setBounds(240, 90, 320, 50);

        Page_Title18.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title18.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title18.setForeground(java.awt.Color.white);
        Page_Title18.setText("Lydia");
        card_pay.add(Page_Title18);
        Page_Title18.setBounds(20, 0, 200, 60);
        card_pay.add(jLabel17);
        jLabel17.setBounds(280, 130, 200, 30);

        jLabel78.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue_48.png"))); // NOI18N
        card_pay.add(jLabel78);
        jLabel78.setBounds(100, 370, 50, 60);

        jLabel79.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/mdpi.png"))); // NOI18N
        card_pay.add(jLabel79);
        jLabel79.setBounds(410, 370, 48, 60);

        jLabel107.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel107.setForeground(new java.awt.Color(153, 153, 153));
        jLabel107.setText("Home / Card / NewCard / Packages / PaymentType / payByCard / Payment /");
        card_pay.add(jLabel107);
        jLabel107.setBounds(10, 490, 600, 17);

        atm_outer.add(card_pay, "card2");

        card_paid.setBackground(new java.awt.Color(34, 40, 40));
        card_paid.setLayout(null);

        Page_Title20.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title20.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title20.setForeground(java.awt.Color.white);
        Page_Title20.setText("Payment Recived");
        card_paid.add(Page_Title20);
        Page_Title20.setBounds(280, 330, 240, 30);

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel20MouseClicked(evt);
            }
        });
        card_paid.add(jLabel20);
        jLabel20.setBounds(730, 10, 40, 30);

        jLabel_paystationSmartAccNum.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel_paystationSmartAccNum.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartAccNum.setText("Smart Account Number");
        jLabel_paystationSmartAccNum.setToolTipText("");
        card_paid.add(jLabel_paystationSmartAccNum);
        jLabel_paystationSmartAccNum.setBounds(150, 110, 220, 30);

        jLabel_paystationSmartAccNum1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel_paystationSmartAccNum1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartAccNum1.setText("Smart Account Number");
        jLabel_paystationSmartAccNum1.setToolTipText("");
        card_paid.add(jLabel_paystationSmartAccNum1);
        jLabel_paystationSmartAccNum1.setBounds(410, 110, 220, 30);

        jLabel_paystationSmartCard.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel_paystationSmartCard.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCard.setText("Smart Card Id");
        jLabel_paystationSmartCard.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCard);
        jLabel_paystationSmartCard.setBounds(150, 150, 180, 30);

        jLabel_paystationSmartCard1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel_paystationSmartCard1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCard1.setText("Smart Card Id");
        jLabel_paystationSmartCard1.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCard1);
        jLabel_paystationSmartCard1.setBounds(410, 150, 140, 30);

        jLabel_paystationSmartCardPack.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel_paystationSmartCardPack.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCardPack.setText("Smart Card package");
        jLabel_paystationSmartCardPack.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCardPack);
        jLabel_paystationSmartCardPack.setBounds(150, 200, 220, 30);

        jLabel_paystationSmartCardPack1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel_paystationSmartCardPack1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCardPack1.setText("Smart Card package");
        jLabel_paystationSmartCardPack1.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCardPack1);
        jLabel_paystationSmartCardPack1.setBounds(410, 200, 210, 30);

        jLabel_paystationSmartCardBalance.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel_paystationSmartCardBalance.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCardBalance.setText("Account Balance");
        jLabel_paystationSmartCardBalance.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCardBalance);
        jLabel_paystationSmartCardBalance.setBounds(150, 240, 200, 30);

        jLabel_paystationSmartCardBalance1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel_paystationSmartCardBalance1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel_paystationSmartCardBalance1.setText("Account Balance");
        jLabel_paystationSmartCardBalance1.setToolTipText("");
        card_paid.add(jLabel_paystationSmartCardBalance1);
        jLabel_paystationSmartCardBalance1.setBounds(410, 240, 210, 30);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel15.setText("Add");
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
        });
        card_paid.add(jLabel15);
        jLabel15.setBounds(0, 0, 790, 80);
        card_paid.add(jLabel19);
        jLabel19.setBounds(300, 120, 200, 30);

        Page_Title23.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title23.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title23.setForeground(java.awt.Color.white);
        Page_Title23.setText("Thank You");
        card_paid.add(Page_Title23);
        Page_Title23.setBounds(160, 330, 130, 30);

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
        Page_Title25.setBounds(290, 370, 370, 30);
        card_paid.add(jLabel22);
        jLabel22.setBounds(280, 130, 200, 30);

        jLabel108.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel108.setForeground(new java.awt.Color(153, 153, 153));
        jLabel108.setText("Home / Card / NewCard / Packages / PaymentType / payByCard / Payment / Summery");
        card_paid.add(jLabel108);
        jLabel108.setBounds(10, 490, 600, 17);

        atm_outer.add(card_paid, "card2");

        cash_pay.setBackground(new java.awt.Color(34, 40, 40));
        cash_pay.setLayout(null);

        Page_Title21.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title21.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title21.setForeground(java.awt.Color.white);
        Page_Title21.setText("Total Amount to Pay Rs. 0.00");
        cash_pay.add(Page_Title21);
        Page_Title21.setBounds(220, 190, 380, 30);

        jLabel_paystaionCashPayHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionCashPayHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCashPayHomeMouseClicked(evt);
            }
        });
        cash_pay.add(jLabel_paystaionCashPayHome);
        jLabel_paystaionCashPayHome.setBounds(730, 10, 40, 30);

        blue41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        cash_pay.add(blue41);
        blue41.setBounds(20, 580, 60, 25);

        blue42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        cash_pay.add(blue42);
        blue42.setBounds(80, 580, 60, 25);

        blue43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        cash_pay.add(blue43);
        blue43.setBounds(130, 580, 60, 25);

        blue44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue4.png"))); // NOI18N
        cash_pay.add(blue44);
        blue44.setBounds(190, 580, 60, 25);

        gray41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        cash_pay.add(gray41);
        gray41.setBounds(20, 580, 60, 25);

        gray42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        cash_pay.add(gray42);
        gray42.setBounds(80, 580, 60, 25);

        gray43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        cash_pay.add(gray43);
        gray43.setBounds(130, 580, 60, 25);

        gray44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        cash_pay.add(gray44);
        gray44.setBounds(190, 580, 60, 25);

        jLabel_paystaionCashPayBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCashPayBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCashPayBackMouseClicked(evt);
            }
        });
        cash_pay.add(jLabel_paystaionCashPayBack);
        jLabel_paystaionCashPayBack.setBounds(680, 10, 30, 30);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/callout-red-bg (2).png"))); // NOI18N
        jLabel12.setText("Add");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        cash_pay.add(jLabel12);
        jLabel12.setBounds(410, 310, 290, 60);

        Page_Title26.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title26.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title26.setForeground(java.awt.Color.white);
        Page_Title26.setText("Cancel The Payment");
        Page_Title26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Page_Title26MouseClicked(evt);
            }
        });
        cash_pay.add(Page_Title26);
        Page_Title26.setBounds(470, 310, 230, 60);

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel27.setText("Add");
        jLabel27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel27MouseClicked(evt);
            }
        });
        cash_pay.add(jLabel27);
        jLabel27.setBounds(0, 0, 800, 80);

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel28.setText("Add");
        jLabel28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel28MouseClicked(evt);
            }
        });
        cash_pay.add(jLabel28);
        jLabel28.setBounds(100, 310, 300, 60);

        Page_Title27.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title27.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title27.setForeground(java.awt.Color.white);
        Page_Title27.setText("Accept The Payment");
        cash_pay.add(Page_Title27);
        Page_Title27.setBounds(170, 320, 290, 40);

        Page_Title28.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title28.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title28.setForeground(java.awt.Color.white);
        Page_Title28.setText("Pay By Cash");
        cash_pay.add(Page_Title28);
        Page_Title28.setBounds(40, 90, 240, 30);

        Page_Title29.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title29.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title29.setForeground(java.awt.Color.white);
        Page_Title29.setText("Lydia ");
        cash_pay.add(Page_Title29);
        Page_Title29.setBounds(20, 0, 360, 60);
        cash_pay.add(jLabel30);
        jLabel30.setBounds(280, 130, 200, 30);

        jLabel109.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue_48.png"))); // NOI18N
        cash_pay.add(jLabel109);
        jLabel109.setBounds(110, 300, 60, 80);

        jLabel110.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/mdpi.png"))); // NOI18N
        cash_pay.add(jLabel110);
        jLabel110.setBounds(420, 310, 50, 60);

        jLabel111.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel111.setForeground(new java.awt.Color(153, 153, 153));
        jLabel111.setText("Home / Card / NewCard / Packages / PaymentType / ByCash /");
        cash_pay.add(jLabel111);
        jLabel111.setBounds(10, 490, 440, 17);

        atm_outer.add(cash_pay, "card2");

        select_package.setBackground(new java.awt.Color(34, 40, 40));
        select_package.setForeground(new java.awt.Color(34, 40, 40));
        select_package.setLayout(null);

        Page_Title39.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title39.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title39.setForeground(java.awt.Color.white);
        Page_Title39.setText("Mega package");
        select_package.add(Page_Title39);
        Page_Title39.setBounds(460, 310, 170, 40);

        jLabel31.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel31.setForeground(java.awt.Color.white);
        jLabel31.setText("Peak");
        select_package.add(jLabel31);
        jLabel31.setBounds(140, 210, 50, 22);

        jLabel46.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46.setForeground(java.awt.Color.white);
        jLabel46.setText("Offpeak");
        select_package.add(jLabel46);
        jLabel46.setBounds(140, 180, 70, 22);

        jLabel52OffpeakAmount.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel52OffpeakAmount.setForeground(java.awt.Color.white);
        jLabel52OffpeakAmount.setText("0");
        select_package.add(jLabel52OffpeakAmount);
        jLabel52OffpeakAmount.setBounds(300, 180, 90, 20);

        jLabel59PeakAmount.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel59PeakAmount.setForeground(java.awt.Color.white);
        jLabel59PeakAmount.setText("0");
        select_package.add(jLabel59PeakAmount);
        jLabel59PeakAmount.setBounds(300, 210, 100, 22);

        jLabel52.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel52.setForeground(java.awt.Color.white);
        jLabel52.setText("Peak");
        select_package.add(jLabel52);
        jLabel52.setBounds(500, 210, 60, 22);

        jLabel88AmountNeed.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel88AmountNeed.setForeground(java.awt.Color.white);
        jLabel88AmountNeed.setText("0");
        select_package.add(jLabel88AmountNeed);
        jLabel88AmountNeed.setBounds(300, 250, 60, 22);

        jLabel88AmountNeed1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel88AmountNeed1.setForeground(java.awt.Color.white);
        jLabel88AmountNeed1.setText("0");
        select_package.add(jLabel88AmountNeed1);
        jLabel88AmountNeed1.setBounds(660, 250, 60, 22);

        jLabel88AmountNeed2.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel88AmountNeed2.setForeground(java.awt.Color.white);
        jLabel88AmountNeed2.setText("0");
        select_package.add(jLabel88AmountNeed2);
        jLabel88AmountNeed2.setBounds(300, 440, 60, 22);

        jLabel88AmountNeed3.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel88AmountNeed3.setForeground(java.awt.Color.white);
        jLabel88AmountNeed3.setText("0");
        select_package.add(jLabel88AmountNeed3);
        jLabel88AmountNeed3.setBounds(670, 440, 60, 22);

        jLabel94.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel94.setForeground(java.awt.Color.white);
        jLabel94.setText("Charge");
        select_package.add(jLabel94);
        jLabel94.setBounds(470, 440, 80, 22);

        jLabel89.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel89.setForeground(java.awt.Color.white);
        jLabel89.setText("Charge");
        select_package.add(jLabel89);
        jLabel89.setBounds(140, 250, 80, 22);

        jLabel88.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel88.setForeground(java.awt.Color.white);
        jLabel88.setText("Charge");
        select_package.add(jLabel88);
        jLabel88.setBounds(500, 250, 100, 22);

        jLabel85.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel85.setForeground(java.awt.Color.white);
        jLabel85.setText("Charge");
        select_package.add(jLabel85);
        jLabel85.setBounds(110, 440, 90, 22);

        jLabel59.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel59.setForeground(java.awt.Color.white);
        jLabel59.setText("Offpeak");
        select_package.add(jLabel59);
        jLabel59.setBounds(500, 170, 70, 22);

        jLabel52OffpeakAmount1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel52OffpeakAmount1.setForeground(java.awt.Color.white);
        jLabel52OffpeakAmount1.setText("0");
        select_package.add(jLabel52OffpeakAmount1);
        jLabel52OffpeakAmount1.setBounds(660, 170, 110, 22);

        jLabel59PeakAmount1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel59PeakAmount1.setForeground(java.awt.Color.white);
        jLabel59PeakAmount1.setText("0");
        select_package.add(jLabel59PeakAmount1);
        jLabel59PeakAmount1.setBounds(660, 210, 110, 22);

        jLabel60.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel60.setForeground(java.awt.Color.white);
        jLabel60.setText("Peak");
        select_package.add(jLabel60);
        jLabel60.setBounds(110, 400, 60, 22);

        jLabel61.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel61.setForeground(java.awt.Color.white);
        jLabel61.setText("Offpeak");
        select_package.add(jLabel61);
        jLabel61.setBounds(110, 360, 70, 22);

        jLabel52OffpeakAmount2.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel52OffpeakAmount2.setForeground(java.awt.Color.white);
        jLabel52OffpeakAmount2.setText("0");
        select_package.add(jLabel52OffpeakAmount2);
        jLabel52OffpeakAmount2.setBounds(300, 360, 110, 22);

        jLabel59PeakAmount2.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel59PeakAmount2.setForeground(java.awt.Color.white);
        jLabel59PeakAmount2.setText("0");
        select_package.add(jLabel59PeakAmount2);
        jLabel59PeakAmount2.setBounds(300, 400, 110, 22);

        jLabel62.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel62.setForeground(java.awt.Color.white);
        jLabel62.setText("Peak");
        select_package.add(jLabel62);
        jLabel62.setBounds(470, 410, 70, 22);

        blue11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        select_package.add(blue11);
        blue11.setBounds(150, 530, 60, 25);

        blue12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        select_package.add(blue12);
        blue12.setBounds(210, 530, 60, 25);

        gray11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        select_package.add(gray11);
        gray11.setBounds(150, 530, 60, 25);

        gray12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        select_package.add(gray12);
        gray12.setBounds(210, 530, 60, 25);

        gray13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        select_package.add(gray13);
        gray13.setBounds(260, 530, 60, 25);

        gray14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        select_package.add(gray14);
        gray14.setBounds(320, 530, 60, 25);

        blue13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        select_package.add(blue13);
        blue13.setBounds(260, 530, 60, 25);

        blue14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        select_package.add(blue14);
        blue14.setBounds(320, 530, 60, 25);

        jLabel63.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel63.setForeground(java.awt.Color.white);
        jLabel63.setText("Offpeak");
        select_package.add(jLabel63);
        jLabel63.setBounds(470, 370, 80, 22);

        jLabel52OffpeakAmount3.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel52OffpeakAmount3.setForeground(java.awt.Color.white);
        jLabel52OffpeakAmount3.setText("0");
        select_package.add(jLabel52OffpeakAmount3);
        jLabel52OffpeakAmount3.setBounds(670, 370, 110, 15);

        jLabel59PeakAmount3.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel59PeakAmount3.setForeground(java.awt.Color.white);
        jLabel59PeakAmount3.setText("0");
        select_package.add(jLabel59PeakAmount3);
        jLabel59PeakAmount3.setBounds(670, 402, 110, 30);

        jLabel_paystaionPackageHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionPackageHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageHomeMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystaionPackageHome);
        jLabel_paystaionPackageHome.setBounds(720, 10, 30, 30);

        jLabel_paystaionPackageBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionPackageBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageBackMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystaionPackageBack);
        jLabel_paystaionPackageBack.setBounds(680, 10, 40, 30);

        jLabel_paystationMegaPack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationMegaPack.setText("Add");
        jLabel_paystationMegaPack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationMegaPackMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystationMegaPack);
        jLabel_paystationMegaPack.setBounds(440, 310, 300, 170);

        Page_Title34.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title34.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title34.setForeground(java.awt.Color.white);
        Page_Title34.setText("Night Package");
        select_package.add(Page_Title34);
        Page_Title34.setBounds(100, 120, 170, 40);

        Page_Title36.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title36.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title36.setForeground(java.awt.Color.white);
        Page_Title36.setText("Budget Package");
        select_package.add(Page_Title36);
        Page_Title36.setBounds(100, 310, 200, 40);

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
        jLabel_paystationBudjectPack.setBounds(80, 310, 310, 170);

        jLabel_paystationNightPack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack.setText("Add");
        jLabel_paystationNightPack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPackMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystationNightPack);
        jLabel_paystationNightPack.setBounds(80, 120, 310, 170);

        Page_Title35.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title35.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title35.setForeground(java.awt.Color.white);
        Page_Title35.setText("Day Package");
        select_package.add(Page_Title35);
        Page_Title35.setBounds(460, 120, 170, 40);

        Page_Title37.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title37.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title37.setForeground(java.awt.Color.white);
        Page_Title37.setText("Lydia");
        select_package.add(Page_Title37);
        Page_Title37.setBounds(20, 0, 230, 60);

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel34.setText("Add");
        jLabel34.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel34MouseClicked(evt);
            }
        });
        select_package.add(jLabel34);
        jLabel34.setBounds(0, 0, 810, 80);

        jLabel_paystationDayPack.setForeground(new java.awt.Color(153, 0, 51));
        jLabel_paystationDayPack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationDayPack.setText("Add");
        jLabel_paystationDayPack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationDayPackMouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystationDayPack);
        jLabel_paystationDayPack.setBounds(440, 120, 300, 170);

        Page_Title65.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title65.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title65.setForeground(java.awt.Color.white);
        Page_Title65.setText("select a package for new smart card ");
        select_package.add(Page_Title65);
        Page_Title65.setBounds(20, 80, 420, 30);

        jLabel104.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel104.setForeground(new java.awt.Color(153, 153, 153));
        jLabel104.setText("Home/Card/NewCard/Packages");
        select_package.add(jLabel104);
        jLabel104.setBounds(10, 490, 230, 17);

        jLabel_paystationNightPack4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack4.setText("Add");
        jLabel_paystationNightPack4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPack4MouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystationNightPack4);
        jLabel_paystationNightPack4.setBounds(80, 120, 310, 40);

        jLabel_paystationDayPack2.setForeground(new java.awt.Color(153, 0, 51));
        jLabel_paystationDayPack2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationDayPack2.setText("Add");
        jLabel_paystationDayPack2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationDayPack2MouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystationDayPack2);
        jLabel_paystationDayPack2.setBounds(440, 120, 300, 40);

        jLabel_paystationBudjectPack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationBudjectPack1.setText("Add");
        jLabel_paystationBudjectPack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationBudjectPack1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel_paystationBudjectPack1MouseEntered(evt);
            }
        });
        select_package.add(jLabel_paystationBudjectPack1);
        jLabel_paystationBudjectPack1.setBounds(80, 310, 310, 40);

        jLabel_paystationMegaPack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationMegaPack1.setText("Add");
        jLabel_paystationMegaPack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationMegaPack1MouseClicked(evt);
            }
        });
        select_package.add(jLabel_paystationMegaPack1);
        jLabel_paystationMegaPack1.setBounds(440, 310, 300, 40);

        atm_outer.add(select_package, "card2");

        jPanel_selectSmartCradOptions.setBackground(new java.awt.Color(34, 40, 40));
        jPanel_selectSmartCradOptions.setLayout(null);

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel37.setText("Add");
        jLabel37.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel37MouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel37);
        jLabel37.setBounds(60, 190, 300, 210);

        jLabel_paystaionSmartCardOptionHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionSmartCardOptionHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionSmartCardOptionHomeMouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel_paystaionSmartCardOptionHome);
        jLabel_paystaionSmartCardOptionHome.setBounds(735, 10, 40, 30);

        jLabel_paystaionSmartCardOptionBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionSmartCardOptionBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionSmartCardOptionBackMouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel_paystaionSmartCardOptionBack);
        jLabel_paystaionSmartCardOptionBack.setBounds(690, 10, 40, 30);

        Page_Title40.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title40.setFont(new java.awt.Font("Ubuntu Light", 0, 28)); // NOI18N
        Page_Title40.setForeground(java.awt.Color.white);
        Page_Title40.setText("Select An Option");
        jPanel_selectSmartCradOptions.add(Page_Title40);
        Page_Title40.setBounds(40, 110, 360, 30);

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel38.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel38MouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel38);
        jLabel38.setBounds(400, 190, 310, 210);

        jLable_newSmartCard.setBackground(new java.awt.Color(102, 102, 102));
        jLable_newSmartCard.setFont(new java.awt.Font("Ubuntu Light", 0, 28)); // NOI18N
        jLable_newSmartCard.setForeground(java.awt.Color.white);
        jLable_newSmartCard.setText("New Smart Card");
        jLable_newSmartCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLable_newSmartCardMouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLable_newSmartCard);
        jLable_newSmartCard.setBounds(100, 360, 220, 40);

        jLable_smartCardTopUp.setBackground(new java.awt.Color(102, 102, 102));
        jLable_smartCardTopUp.setFont(new java.awt.Font("Ubuntu Light", 0, 28)); // NOI18N
        jLable_smartCardTopUp.setForeground(java.awt.Color.white);
        jLable_smartCardTopUp.setText("  Top-Up");
        jPanel_selectSmartCradOptions.add(jLable_smartCardTopUp);
        jLable_smartCardTopUp.setBounds(500, 360, 120, 40);

        Page_Title43.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title43.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title43.setForeground(java.awt.Color.white);
        Page_Title43.setText("Lydia");
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
        jLabel39.setBounds(0, 0, 780, 80);

        jLabel48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel48.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel48MouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel48);
        jLabel48.setBounds(60, 360, 300, 40);

        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel53.setText("Add");
        jLabel53.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel53MouseClicked(evt);
            }
        });
        jPanel_selectSmartCradOptions.add(jLabel53);
        jLabel53.setBounds(400, 360, 310, 40);

        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/icon-72@2x (1).png"))); // NOI18N
        jPanel_selectSmartCradOptions.add(jLabel58);
        jLabel58.setBounds(140, 200, 210, 160);

        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/topUobig.png"))); // NOI18N
        jPanel_selectSmartCradOptions.add(jLabel56);
        jLabel56.setBounds(480, 160, 270, 240);

        jLabel101.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel101.setForeground(new java.awt.Color(153, 153, 153));
        jLabel101.setText("Home/Card/");
        jPanel_selectSmartCradOptions.add(jLabel101);
        jLabel101.setBounds(10, 490, 90, 17);

        atm_outer.add(jPanel_selectSmartCradOptions, "card2");

        enterCardId.setBackground(new java.awt.Color(34, 40, 40));
        enterCardId.setLayout(null);

        paystaionCardIdInvalid.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardIdInvalid.setFont(new java.awt.Font("Ubuntu Light", 0, 14)); // NOI18N
        paystaionCardIdInvalid.setForeground(java.awt.Color.white);
        paystaionCardIdInvalid.setText("Invalid credentials. Try again!");
        enterCardId.add(paystaionCardIdInvalid);
        paystaionCardIdInvalid.setBounds(310, 280, 210, 50);

        paystaionCardIdEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        paystaionCardIdEnter.setText("Add");
        paystaionCardIdEnter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paystaionCardIdEnterMouseClicked(evt);
            }
        });
        enterCardId.add(paystaionCardIdEnter);
        paystaionCardIdEnter.setBounds(270, 340, 260, 60);

        blue1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        enterCardId.add(blue1);
        blue1.setBounds(180, 540, 60, 25);

        gray1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        enterCardId.add(gray1);
        gray1.setBounds(180, 540, 60, 25);

        gray2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        enterCardId.add(gray2);
        gray2.setBounds(240, 540, 60, 25);

        gray3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        enterCardId.add(gray3);
        gray3.setBounds(290, 540, 60, 25);

        gray4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        enterCardId.add(gray4);
        gray4.setBounds(350, 540, 60, 25);

        gray5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray5.png"))); // NOI18N
        enterCardId.add(gray5);
        gray5.setBounds(410, 540, 60, 25);

        blue2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        enterCardId.add(blue2);
        blue2.setBounds(240, 540, 60, 25);

        blue3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        enterCardId.add(blue3);
        blue3.setBounds(290, 540, 60, 25);

        blue4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue4.png"))); // NOI18N
        enterCardId.add(blue4);
        blue4.setBounds(350, 540, 60, 25);

        blue5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue5.png"))); // NOI18N
        enterCardId.add(blue5);
        blue5.setBounds(410, 540, 60, 25);

        paystaionCardId2.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId2.setFont(new java.awt.Font("Ubuntu Light", 0, 27)); // NOI18N
        paystaionCardId2.setForeground(java.awt.Color.white);
        paystaionCardId2.setText("Please Enter Your Smart Card ID & PIN");
        enterCardId.add(paystaionCardId2);
        paystaionCardId2.setBounds(30, 90, 490, 30);

        jLabel_paystaionCardPayHome1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionCardPayHome1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayHome1MouseClicked(evt);
            }
        });
        enterCardId.add(jLabel_paystaionCardPayHome1);
        jLabel_paystaionCardPayHome1.setBounds(720, 10, 30, 30);

        jLabel_paystaionCardPayBack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCardPayBack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayBack1MouseClicked(evt);
            }
        });
        enterCardId.add(jLabel_paystaionCardPayBack1);
        jLabel_paystaionCardPayBack1.setBounds(680, 10, 30, 30);

        paystaionCardIdInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystaionCardIdInputActionPerformed(evt);
            }
        });
        enterCardId.add(paystaionCardIdInput);
        paystaionCardIdInput.setBounds(280, 160, 230, 40);

        paystaionCardPinTopUp.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardPinTopUp.setFont(new java.awt.Font("Ubuntu Light", 0, 26)); // NOI18N
        paystaionCardPinTopUp.setForeground(java.awt.Color.white);
        paystaionCardPinTopUp.setText("Submit");
        enterCardId.add(paystaionCardPinTopUp);
        paystaionCardPinTopUp.setBounds(360, 340, 100, 60);

        paystaionCardPinTopUp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystaionCardPinTopUp1ActionPerformed(evt);
            }
        });
        enterCardId.add(paystaionCardPinTopUp1);
        paystaionCardPinTopUp1.setBounds(280, 250, 230, 40);

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel32.setText("Add");
        jLabel32.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel32MouseClicked(evt);
            }
        });
        enterCardId.add(jLabel32);
        jLabel32.setBounds(0, 0, 770, 80);

        paystaionCardId.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        paystaionCardId.setForeground(java.awt.Color.white);
        paystaionCardId.setText("Card ID");
        enterCardId.add(paystaionCardId);
        paystaionCardId.setBounds(360, 130, 100, 28);

        Page_Title47.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title47.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title47.setForeground(java.awt.Color.white);
        Page_Title47.setText("Lydia");
        enterCardId.add(Page_Title47);
        Page_Title47.setBounds(20, 0, 280, 60);
        enterCardId.add(jLabel42);
        jLabel42.setBounds(280, 130, 200, 30);

        paystaionCardPinTopUp2.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardPinTopUp2.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        paystaionCardPinTopUp2.setForeground(java.awt.Color.white);
        paystaionCardPinTopUp2.setText("PIN");
        enterCardId.add(paystaionCardPinTopUp2);
        paystaionCardPinTopUp2.setBounds(380, 220, 50, 28);

        jLabel77.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue_48.png"))); // NOI18N
        enterCardId.add(jLabel77);
        jLabel77.setBounds(290, 340, 48, 60);

        jLabel113.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel113.setForeground(new java.awt.Color(153, 153, 153));
        jLabel113.setText("ex - SC123");
        enterCardId.add(jLabel113);
        jLabel113.setBounds(280, 200, 80, 17);

        jLabel114.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel114.setForeground(new java.awt.Color(153, 153, 153));
        jLabel114.setText("Home / TopUp / ");
        enterCardId.add(jLabel114);
        jLabel114.setBounds(10, 490, 200, 17);

        atm_outer.add(enterCardId, "card2");

        enterCardPin.setBackground(new java.awt.Color(34, 40, 40));
        enterCardPin.setLayout(null);

        paystaionCardPinNoMatch.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardPinNoMatch.setFont(new java.awt.Font("Ubuntu Light", 0, 14)); // NOI18N
        paystaionCardPinNoMatch.setForeground(java.awt.Color.white);
        paystaionCardPinNoMatch.setText("PIN numbers doesn't match ");
        enterCardPin.add(paystaionCardPinNoMatch);
        paystaionCardPinNoMatch.setBounds(310, 310, 210, 50);

        paystaionCardId3.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId3.setFont(new java.awt.Font("Ubuntu Light", 0, 27)); // NOI18N
        paystaionCardId3.setForeground(java.awt.Color.white);
        paystaionCardId3.setText("Enter Your PIN for The Card");
        enterCardPin.add(paystaionCardId3);
        paystaionCardId3.setBounds(40, 90, 450, 40);

        blue6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        enterCardPin.add(blue6);
        blue6.setBounds(150, 530, 60, 25);

        gray6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        enterCardPin.add(gray6);
        gray6.setBounds(150, 530, 60, 25);

        gray7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        enterCardPin.add(gray7);
        gray7.setBounds(210, 530, 60, 25);

        gray8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        enterCardPin.add(gray8);
        gray8.setBounds(260, 530, 60, 25);

        gray9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        enterCardPin.add(gray9);
        gray9.setBounds(320, 530, 60, 25);

        blue7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        enterCardPin.add(blue7);
        blue7.setBounds(210, 530, 60, 25);

        blue8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        enterCardPin.add(blue8);
        blue8.setBounds(260, 530, 60, 25);

        blue9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        enterCardPin.add(blue9);
        blue9.setBounds(320, 530, 60, 25);

        jLabel_paystaionCardPayHome2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionCardPayHome2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayHome2MouseClicked(evt);
            }
        });
        enterCardPin.add(jLabel_paystaionCardPayHome2);
        jLabel_paystaionCardPayHome2.setBounds(735, 10, 30, 30);

        jLabel_paystaionCardPayBack2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCardPayBack2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayBack2MouseClicked(evt);
            }
        });
        enterCardPin.add(jLabel_paystaionCardPayBack2);
        jLabel_paystaionCardPayBack2.setBounds(690, 10, 30, 30);

        paystaionCardPinInput2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystaionCardPinInput2ActionPerformed(evt);
            }
        });
        enterCardPin.add(paystaionCardPinInput2);
        paystaionCardPinInput2.setBounds(290, 270, 200, 40);

        paystaionCardPinInput1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystaionCardPinInput1ActionPerformed(evt);
            }
        });
        enterCardPin.add(paystaionCardPinInput1);
        paystaionCardPinInput1.setBounds(290, 170, 200, 40);

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel36.setText("Add");
        jLabel36.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel36MouseClicked(evt);
            }
        });
        enterCardPin.add(jLabel36);
        jLabel36.setBounds(0, 0, 790, 70);
        enterCardPin.add(jLabel43);
        jLabel43.setBounds(300, 100, 200, 30);

        paystaionCardPinSubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        paystaionCardPinSubmit.setText("Add");
        paystaionCardPinSubmit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paystaionCardPinSubmitMouseClicked(evt);
            }
        });
        enterCardPin.add(paystaionCardPinSubmit);
        paystaionCardPinSubmit.setBounds(270, 380, 240, 60);

        paystaionCardIdEnterText1.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardIdEnterText1.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        paystaionCardIdEnterText1.setForeground(java.awt.Color.white);
        paystaionCardIdEnterText1.setText("Submit");
        enterCardPin.add(paystaionCardIdEnterText1);
        paystaionCardIdEnterText1.setBounds(370, 380, 100, 50);

        paystaionCardId1.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId1.setFont(new java.awt.Font("Ubuntu Light", 0, 14)); // NOI18N
        paystaionCardId1.setForeground(new java.awt.Color(102, 102, 102));
        paystaionCardId1.setText("Tips : Note Down Your Pin Number For The Future Use");
        enterCardPin.add(paystaionCardId1);
        paystaionCardId1.setBounds(210, 450, 340, 28);

        paystaionCardId4.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId4.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        paystaionCardId4.setForeground(java.awt.Color.white);
        paystaionCardId4.setText("PIN");
        enterCardPin.add(paystaionCardId4);
        paystaionCardId4.setBounds(360, 140, 100, 28);

        Page_Title48.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title48.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title48.setForeground(java.awt.Color.white);
        Page_Title48.setText("Lydia");
        enterCardPin.add(Page_Title48);
        Page_Title48.setBounds(20, 0, 150, 60);
        enterCardPin.add(jLabel45);
        jLabel45.setBounds(280, 130, 200, 30);

        jLabel102.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel102.setForeground(new java.awt.Color(153, 153, 153));
        jLabel102.setText("Home/Card/NewCard");
        enterCardPin.add(jLabel102);
        jLabel102.setBounds(10, 490, 160, 17);

        jLabel103.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue_48.png"))); // NOI18N
        enterCardPin.add(jLabel103);
        jLabel103.setBounds(290, 380, 60, 60);

        paystaionCardId5.setBackground(new java.awt.Color(102, 102, 102));
        paystaionCardId5.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        paystaionCardId5.setForeground(java.awt.Color.white);
        paystaionCardId5.setText("RE-ENTER PIN");
        enterCardPin.add(paystaionCardId5);
        paystaionCardId5.setBounds(310, 230, 180, 28);

        atm_outer.add(enterCardPin, "card2");

        paystationPassiveCardNormal.setBackground(new java.awt.Color(34, 40, 40));
        paystationPassiveCardNormal.setForeground(new java.awt.Color(255, 255, 255));
        paystationPassiveCardNormal.setLayout(null);

        paystationNormalOffpeak.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        paystationNormalOffpeak.setForeground(new java.awt.Color(255, 255, 255));
        paystationNormalOffpeak.setText("Off-Peak");
        paystationPassiveCardNormal.add(paystationNormalOffpeak);
        paystationNormalOffpeak.setBounds(440, 100, 120, 50);

        paystationNormalOffpeakOutput.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        paystationNormalOffpeakOutput.setForeground(new java.awt.Color(255, 255, 255));
        paystationNormalOffpeakOutput.setText("1.00");
        paystationPassiveCardNormal.add(paystationNormalOffpeakOutput);
        paystationNormalOffpeakOutput.setBounds(580, 120, 70, 20);

        paystationNormalPeakOutput.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        paystationNormalPeakOutput.setForeground(new java.awt.Color(255, 255, 255));
        paystationNormalPeakOutput.setText("1.00");
        paystationPassiveCardNormal.add(paystationNormalPeakOutput);
        paystationNormalPeakOutput.setBounds(580, 180, 70, 20);

        blue36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        paystationPassiveCardNormal.add(blue36);
        blue36.setBounds(270, 520, 60, 25);

        blue37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        paystationPassiveCardNormal.add(blue37);
        blue37.setBounds(330, 520, 60, 25);

        gray36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        paystationPassiveCardNormal.add(gray36);
        gray36.setBounds(270, 520, 60, 25);

        gray37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        paystationPassiveCardNormal.add(gray37);
        gray37.setBounds(330, 520, 60, 25);

        gray38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        paystationPassiveCardNormal.add(gray38);
        gray38.setBounds(380, 520, 60, 25);

        gray39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        paystationPassiveCardNormal.add(gray39);
        gray39.setBounds(440, 520, 60, 25);

        blue38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        paystationPassiveCardNormal.add(blue38);
        blue38.setBounds(380, 520, 60, 25);

        blue39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        paystationPassiveCardNormal.add(blue39);
        blue39.setBounds(440, 520, 60, 25);

        paystationNormalPeak.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        paystationNormalPeak.setForeground(new java.awt.Color(255, 255, 255));
        paystationNormalPeak.setText("Peak");
        paystationPassiveCardNormal.add(paystationNormalPeak);
        paystationNormalPeak.setBounds(440, 160, 90, 40);

        passiveCardNormalNext.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        passiveCardNormalNext.setForeground(new java.awt.Color(255, 255, 255));
        passiveCardNormalNext.setText("              Next");
        passiveCardNormalNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                passiveCardNormalNextMouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(passiveCardNormalNext);
        passiveCardNormalNext.setBounds(440, 290, 270, 60);

        paystationPassiveNormalError.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        paystationPassiveNormalError.setForeground(new java.awt.Color(255, 255, 255));
        paystationPassiveNormalError.setText("Please select sourse and a destination to continue");
        paystationPassiveCardNormal.add(paystationPassiveNormalError);
        paystationPassiveNormalError.setBounds(30, 370, 400, 30);

        passivceCardNormalCheck.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        passivceCardNormalCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        passivceCardNormalCheck.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                passivceCardNormalCheckMouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(passivceCardNormalCheck);
        passivceCardNormalCheck.setBounds(30, 290, 230, 60);

        passiveCardTotal.setBackground(java.awt.Color.white);
        passiveCardTotal.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        passiveCardTotal.setForeground(java.awt.Color.white);
        passiveCardTotal.setText("         -");
        paystationPassiveCardNormal.add(passiveCardTotal);
        passiveCardTotal.setBounds(580, 220, 130, 40);

        jLabel49.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(255, 255, 255));
        jLabel49.setText("Check Fare");
        paystationPassiveCardNormal.add(jLabel49);
        jLabel49.setBounds(100, 299, 150, 40);

        passiveCardSourse.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        passiveCardSourse.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "select", "Pettah" }));
        passiveCardSourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passiveCardSourseActionPerformed(evt);
            }
        });
        paystationPassiveCardNormal.add(passiveCardSourse);
        passiveCardSourse.setBounds(30, 150, 160, 32);

        passiveCardDestination.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        passiveCardDestination.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "select", "Panadura" }));
        passiveCardDestination.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passiveCardDestinationActionPerformed(evt);
            }
        });
        paystationPassiveCardNormal.add(passiveCardDestination);
        passiveCardDestination.setBounds(30, 230, 160, 32);

        jLabel_paystaionPaymentHome1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionPaymentHome1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPaymentHome1MouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(jLabel_paystaionPaymentHome1);
        jLabel_paystaionPaymentHome1.setBounds(720, 10, 30, 30);

        jLabel_paystaionPaymentBack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionPaymentBack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPaymentBack1MouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(jLabel_paystaionPaymentBack1);
        jLabel_paystaionPaymentBack1.setBounds(680, 10, 30, 30);

        Page_Title44.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title44.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title44.setForeground(java.awt.Color.white);
        Page_Title44.setText("Lydia");
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
        jLabel47.setBounds(440, 290, 270, 60);

        Page_Title45.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title45.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 24)); // NOI18N
        Page_Title45.setForeground(java.awt.Color.white);
        Page_Title45.setText("Sourse");
        paystationPassiveCardNormal.add(Page_Title45);
        Page_Title45.setBounds(30, 110, 120, 30);

        jLabel69.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel69.setText("Add");
        jLabel69.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel69MouseClicked(evt);
            }
        });
        paystationPassiveCardNormal.add(jLabel69);
        jLabel69.setBounds(0, 0, 770, 70);

        jLabel72.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(255, 255, 255));
        jLabel72.setText("Destination");
        paystationPassiveCardNormal.add(jLabel72);
        jLabel72.setBounds(30, 200, 180, 29);

        Hariya.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue_48.png"))); // NOI18N
        paystationPassiveCardNormal.add(Hariya);
        Hariya.setBounds(40, 290, 104, 60);

        journeyAmount1.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        journeyAmount1.setForeground(new java.awt.Color(204, 204, 204));
        journeyAmount1.setText("Tips : Select Your Souce Then Destination To Calculate Fair");
        paystationPassiveCardNormal.add(journeyAmount1);
        journeyAmount1.setBounds(30, 460, 420, 17);

        journeyAmount2.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        journeyAmount2.setForeground(new java.awt.Color(255, 255, 255));
        journeyAmount2.setText("Amount");
        paystationPassiveCardNormal.add(journeyAmount2);
        journeyAmount2.setBounds(440, 230, 93, 29);

        jLabel115.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel115.setForeground(new java.awt.Color(153, 153, 153));
        jLabel115.setText("Home / Ticket / Package / CalFair");
        paystationPassiveCardNormal.add(jLabel115);
        jLabel115.setBounds(10, 490, 260, 17);

        atm_outer.add(paystationPassiveCardNormal, "card2");

        passiveCardPaid.setBackground(new java.awt.Color(34, 40, 40));
        passiveCardPaid.setLayout(null);

        jLabel71.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel71.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel71MouseClicked(evt);
            }
        });
        passiveCardPaid.add(jLabel71);
        jLabel71.setBounds(720, 10, 30, 30);

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel50.setText("Add");
        jLabel50.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel50MouseClicked(evt);
            }
        });
        passiveCardPaid.add(jLabel50);
        jLabel50.setBounds(0, 0, 790, 80);
        passiveCardPaid.add(jLabel51);
        jLabel51.setBounds(280, 130, 200, 30);

        Page_Title49.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title49.setFont(new java.awt.Font("Ubuntu Light", 0, 30)); // NOI18N
        Page_Title49.setForeground(java.awt.Color.white);
        Page_Title49.setText("Thank You..  Your Card Payment Recived");
        passiveCardPaid.add(Page_Title49);
        Page_Title49.setBounds(80, 190, 590, 110);

        Page_Title50.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title50.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title50.setForeground(java.awt.Color.white);
        passiveCardPaid.add(Page_Title50);
        Page_Title50.setBounds(0, -10, 300, 380);

        Page_Title51.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title51.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title51.setForeground(java.awt.Color.white);
        Page_Title51.setText("Lydia");
        passiveCardPaid.add(Page_Title51);
        Page_Title51.setBounds(20, 0, 360, 60);
        passiveCardPaid.add(jLabel54);
        jLabel54.setBounds(280, 130, 200, 30);

        atm_outer.add(passiveCardPaid, "card2");

        passivePackages.setBackground(new java.awt.Color(34, 40, 40));
        passivePackages.setLayout(null);

        jLabel_paystaionPackageHome1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionPackageHome1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageHome1MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystaionPackageHome1);
        jLabel_paystaionPackageHome1.setBounds(720, 10, 30, 30);

        jLabel46Peak4.setBackground(java.awt.Color.white);
        jLabel46Peak4.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46Peak4.setForeground(java.awt.Color.white);
        jLabel46Peak4.setText("Charge");
        passivePackages.add(jLabel46Peak4);
        jLabel46Peak4.setBounds(80, 250, 90, 22);

        jLabel46PeakAmountNeed.setBackground(java.awt.Color.white);
        jLabel46PeakAmountNeed.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46PeakAmountNeed.setForeground(java.awt.Color.white);
        jLabel46PeakAmountNeed.setText("0");
        passivePackages.add(jLabel46PeakAmountNeed);
        jLabel46PeakAmountNeed.setBounds(210, 250, 50, 22);

        jLabel46Peak5.setBackground(java.awt.Color.white);
        jLabel46Peak5.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46Peak5.setForeground(java.awt.Color.white);
        jLabel46Peak5.setText("Charge");
        passivePackages.add(jLabel46Peak5);
        jLabel46Peak5.setBounds(420, 250, 90, 22);

        jLabel46PeakAmountNeed1.setBackground(java.awt.Color.white);
        jLabel46PeakAmountNeed1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46PeakAmountNeed1.setForeground(java.awt.Color.white);
        jLabel46PeakAmountNeed1.setText("0");
        passivePackages.add(jLabel46PeakAmountNeed1);
        jLabel46PeakAmountNeed1.setBounds(570, 250, 50, 22);

        jLabel46Peak6.setBackground(java.awt.Color.white);
        jLabel46Peak6.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46Peak6.setForeground(java.awt.Color.white);
        jLabel46Peak6.setText("Charge");
        passivePackages.add(jLabel46Peak6);
        jLabel46Peak6.setBounds(90, 422, 90, 30);

        jLabel46PeakAmountNeed2.setBackground(java.awt.Color.white);
        jLabel46PeakAmountNeed2.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46PeakAmountNeed2.setForeground(java.awt.Color.white);
        jLabel46PeakAmountNeed2.setText("0");
        passivePackages.add(jLabel46PeakAmountNeed2);
        jLabel46PeakAmountNeed2.setBounds(210, 422, 50, 30);

        jLabel46Peak7.setBackground(java.awt.Color.white);
        jLabel46Peak7.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46Peak7.setForeground(java.awt.Color.white);
        jLabel46Peak7.setText("Charge");
        passivePackages.add(jLabel46Peak7);
        jLabel46Peak7.setBounds(420, 430, 90, 22);

        jLabel46PeakAmountNeed3.setBackground(java.awt.Color.white);
        jLabel46PeakAmountNeed3.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46PeakAmountNeed3.setForeground(java.awt.Color.white);
        jLabel46PeakAmountNeed3.setText("0");
        passivePackages.add(jLabel46PeakAmountNeed3);
        jLabel46PeakAmountNeed3.setBounds(570, 430, 50, 22);

        jLabel31Offpeak2.setBackground(java.awt.Color.white);
        jLabel31Offpeak2.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel31Offpeak2.setForeground(java.awt.Color.white);
        jLabel31Offpeak2.setText("Offpeak");
        passivePackages.add(jLabel31Offpeak2);
        jLabel31Offpeak2.setBounds(90, 360, 68, 22);

        jLabel46Peak2.setBackground(java.awt.Color.white);
        jLabel46Peak2.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46Peak2.setForeground(java.awt.Color.white);
        jLabel46Peak2.setText("Peak");
        passivePackages.add(jLabel46Peak2);
        jLabel46Peak2.setBounds(90, 390, 60, 22);

        jLabel31OffpeakAmount2.setBackground(java.awt.Color.white);
        jLabel31OffpeakAmount2.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel31OffpeakAmount2.setForeground(java.awt.Color.white);
        jLabel31OffpeakAmount2.setText("0");
        passivePackages.add(jLabel31OffpeakAmount2);
        jLabel31OffpeakAmount2.setBounds(210, 360, 80, 22);

        jLabel46PeakAmount2.setBackground(java.awt.Color.white);
        jLabel46PeakAmount2.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46PeakAmount2.setForeground(java.awt.Color.white);
        jLabel46PeakAmount2.setText("0");
        passivePackages.add(jLabel46PeakAmount2);
        jLabel46PeakAmount2.setBounds(210, 390, 70, 22);

        jLabel31Offpeak1.setBackground(java.awt.Color.white);
        jLabel31Offpeak1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel31Offpeak1.setForeground(java.awt.Color.white);
        jLabel31Offpeak1.setText("Offpeak");
        passivePackages.add(jLabel31Offpeak1);
        jLabel31Offpeak1.setBounds(420, 180, 68, 22);

        jLabel46Peak1.setBackground(java.awt.Color.white);
        jLabel46Peak1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46Peak1.setForeground(java.awt.Color.white);
        jLabel46Peak1.setText("Peak");
        passivePackages.add(jLabel46Peak1);
        jLabel46Peak1.setBounds(420, 212, 42, 30);

        jLabel31OffpeakAmount1.setBackground(java.awt.Color.white);
        jLabel31OffpeakAmount1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel31OffpeakAmount1.setForeground(java.awt.Color.white);
        jLabel31OffpeakAmount1.setText("0");
        passivePackages.add(jLabel31OffpeakAmount1);
        jLabel31OffpeakAmount1.setBounds(570, 190, 70, 22);

        jLabel46PeakAmount1.setBackground(java.awt.Color.white);
        jLabel46PeakAmount1.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46PeakAmount1.setForeground(java.awt.Color.white);
        jLabel46PeakAmount1.setText("0");
        passivePackages.add(jLabel46PeakAmount1);
        jLabel46PeakAmount1.setBounds(570, 220, 70, 22);

        jLabel46Peak.setBackground(java.awt.Color.white);
        jLabel46Peak.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46Peak.setForeground(java.awt.Color.white);
        jLabel46Peak.setText("Peak");
        passivePackages.add(jLabel46Peak);
        jLabel46Peak.setBounds(80, 212, 90, 30);

        jLabel46PeakAmount.setBackground(java.awt.Color.white);
        jLabel46PeakAmount.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46PeakAmount.setForeground(java.awt.Color.white);
        jLabel46PeakAmount.setText("0");
        passivePackages.add(jLabel46PeakAmount);
        jLabel46PeakAmount.setBounds(210, 212, 50, 30);

        blue31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        passivePackages.add(blue31);
        blue31.setBounds(100, 520, 60, 25);

        gray31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        passivePackages.add(gray31);
        gray31.setBounds(100, 520, 60, 25);

        gray32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        passivePackages.add(gray32);
        gray32.setBounds(160, 520, 60, 25);

        gray33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        passivePackages.add(gray33);
        gray33.setBounds(210, 520, 60, 25);

        gray34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        passivePackages.add(gray34);
        gray34.setBounds(270, 520, 60, 25);

        gray35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray5.png"))); // NOI18N
        passivePackages.add(gray35);
        gray35.setBounds(330, 520, 60, 25);

        blue32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        passivePackages.add(blue32);
        blue32.setBounds(160, 520, 60, 25);

        blue33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        passivePackages.add(blue33);
        blue33.setBounds(210, 520, 60, 25);

        blue34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        passivePackages.add(blue34);
        blue34.setBounds(270, 520, 60, 25);

        blue35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray5.png"))); // NOI18N
        passivePackages.add(blue35);
        blue35.setBounds(330, 520, 60, 25);

        jLabel31Offpeak3.setBackground(java.awt.Color.white);
        jLabel31Offpeak3.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel31Offpeak3.setForeground(java.awt.Color.white);
        jLabel31Offpeak3.setText("Offpeak");
        passivePackages.add(jLabel31Offpeak3);
        jLabel31Offpeak3.setBounds(420, 370, 68, 22);

        jLabel46Peak3.setBackground(java.awt.Color.white);
        jLabel46Peak3.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46Peak3.setForeground(java.awt.Color.white);
        jLabel46Peak3.setText("Peak");
        passivePackages.add(jLabel46Peak3);
        jLabel46Peak3.setBounds(420, 400, 60, 22);

        jLabel31OffpeakAmount3.setBackground(java.awt.Color.white);
        jLabel31OffpeakAmount3.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel31OffpeakAmount3.setForeground(java.awt.Color.white);
        jLabel31OffpeakAmount3.setText("0");
        passivePackages.add(jLabel31OffpeakAmount3);
        jLabel31OffpeakAmount3.setBounds(570, 370, 40, 22);

        jLabel46PeakAmount3.setBackground(java.awt.Color.white);
        jLabel46PeakAmount3.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel46PeakAmount3.setForeground(java.awt.Color.white);
        jLabel46PeakAmount3.setText("0");
        passivePackages.add(jLabel46PeakAmount3);
        jLabel46PeakAmount3.setBounds(570, 400, 40, 22);

        jLabel31OffpeakAmount.setBackground(java.awt.Color.white);
        jLabel31OffpeakAmount.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel31OffpeakAmount.setForeground(java.awt.Color.white);
        jLabel31OffpeakAmount.setText("0");
        passivePackages.add(jLabel31OffpeakAmount);
        jLabel31OffpeakAmount.setBounds(210, 180, 60, 22);

        jLabel31Offpeak.setBackground(java.awt.Color.white);
        jLabel31Offpeak.setFont(new java.awt.Font("DejaVu Sans", 0, 18)); // NOI18N
        jLabel31Offpeak.setForeground(java.awt.Color.white);
        jLabel31Offpeak.setText("Offpeak");
        passivePackages.add(jLabel31Offpeak);
        jLabel31Offpeak.setBounds(80, 180, 68, 22);

        jLabel_paystaionPackageBack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionPackageBack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageBack1MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystaionPackageBack1);
        jLabel_paystaionPackageBack1.setBounds(680, 10, 29, 30);

        Page_Title55.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title55.setFont(new java.awt.Font("Ubuntu Light", 1, 24)); // NOI18N
        Page_Title55.setForeground(java.awt.Color.white);
        Page_Title55.setText("Normal");
        passivePackages.add(Page_Title55);
        Page_Title55.setBounds(420, 310, 170, 50);

        Page_Title59.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title59.setFont(new java.awt.Font("Ubuntu Light", 1, 24)); // NOI18N
        Page_Title59.setForeground(java.awt.Color.white);
        Page_Title59.setText("Normal");
        passivePackages.add(Page_Title59);
        Page_Title59.setBounds(70, 310, 160, 40);

        jLabel_paystationNightPack3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack3.setText("Add");
        jLabel_paystationNightPack3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPack3MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationNightPack3);
        jLabel_paystationNightPack3.setBounds(60, 320, 280, 150);

        jLabel_paystationNightPack2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack2.setText("Add");
        jLabel_paystationNightPack2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPack2MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationNightPack2);
        jLabel_paystationNightPack2.setBounds(410, 320, 280, 150);

        Page_Title54.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title54.setFont(new java.awt.Font("Ubuntu Light", 1, 24)); // NOI18N
        Page_Title54.setForeground(java.awt.Color.white);
        Page_Title54.setText("Normal");
        passivePackages.add(Page_Title54);
        Page_Title54.setBounds(70, 130, 160, 28);

        jLabel_paystationNightPack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack1.setText("Add");
        jLabel_paystationNightPack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPack1MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationNightPack1);
        jLabel_paystationNightPack1.setBounds(60, 130, 280, 150);

        Page_Title56.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title56.setFont(new java.awt.Font("Ubuntu Light", 1, 24)); // NOI18N
        Page_Title56.setForeground(java.awt.Color.white);
        Page_Title56.setText("One day");
        passivePackages.add(Page_Title56);
        Page_Title56.setBounds(420, 120, 240, 50);

        Page_Title57.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title57.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title57.setForeground(java.awt.Color.white);
        Page_Title57.setText("Lydia");
        passivePackages.add(Page_Title57);
        Page_Title57.setBounds(20, 0, 220, 60);

        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel55.setText("Add");
        jLabel55.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel55MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel55);
        jLabel55.setBounds(0, 0, 760, 70);

        jLabel_paystationDayPack1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationDayPack1.setText("Add");
        jLabel_paystationDayPack1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationDayPack1MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationDayPack1);
        jLabel_paystationDayPack1.setBounds(410, 130, 280, 150);

        Page_Title58.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title58.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title58.setForeground(java.awt.Color.white);
        Page_Title58.setText("Select a package for smart card");
        passivePackages.add(Page_Title58);
        Page_Title58.setBounds(50, 80, 370, 30);

        jLabel112.setFont(new java.awt.Font("DejaVu Sans", 0, 14)); // NOI18N
        jLabel112.setForeground(new java.awt.Color(153, 153, 153));
        jLabel112.setText("Home/Card/");
        passivePackages.add(jLabel112);
        jLabel112.setBounds(10, 480, 90, 17);

        jLabel_paystationNightPack5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack5.setText("Add");
        jLabel_paystationNightPack5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPack5MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationNightPack5);
        jLabel_paystationNightPack5.setBounds(60, 130, 280, 30);

        jLabel_paystationDayPack3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationDayPack3.setText("Add");
        jLabel_paystationDayPack3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationDayPack3MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationDayPack3);
        jLabel_paystationDayPack3.setBounds(410, 130, 280, 30);

        jLabel_paystationNightPack6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack6.setText("Add");
        jLabel_paystationNightPack6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPack6MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationNightPack6);
        jLabel_paystationNightPack6.setBounds(410, 320, 280, 30);

        jLabel_paystationNightPack7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel_paystationNightPack7.setText("Add");
        jLabel_paystationNightPack7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystationNightPack7MouseClicked(evt);
            }
        });
        passivePackages.add(jLabel_paystationNightPack7);
        jLabel_paystationNightPack7.setBounds(60, 320, 280, 30);

        atm_outer.add(passivePackages, "card2");

        passiveCardPayment.setBackground(new java.awt.Color(34, 40, 40));
        passiveCardPayment.setLayout(null);

        jLabel4.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        jLabel4.setForeground(java.awt.Color.white);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel4.setText("Pay");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        passiveCardPayment.add(jLabel4);
        jLabel4.setBounds(100, 310, 220, 60);

        jLabel64.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        jLabel64.setForeground(java.awt.Color.white);
        jLabel64.setText("Cancel");
        jLabel64.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel64MouseClicked(evt);
            }
        });
        passiveCardPayment.add(jLabel64);
        jLabel64.setBounds(420, 310, 170, 60);

        jLabel5.setFont(new java.awt.Font("DejaVu Sans", 0, 36)); // NOI18N
        jLabel5.setForeground(java.awt.Color.white);
        jLabel5.setText("Total Amount ");
        passiveCardPayment.add(jLabel5);
        jLabel5.setBounds(230, 120, 290, 40);

        passiveCardPayAmount.setFont(new java.awt.Font("DejaVu Sans", 0, 36)); // NOI18N
        passiveCardPayAmount.setForeground(java.awt.Color.white);
        passiveCardPayAmount.setText("0.00");
        passiveCardPayment.add(passiveCardPayAmount);
        passiveCardPayAmount.setBounds(310, 170, 90, 100);

        jLabel_paystaionPackageHome2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionPackageHome2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageHome2MouseClicked(evt);
            }
        });
        passiveCardPayment.add(jLabel_paystaionPackageHome2);
        jLabel_paystaionPackageHome2.setBounds(660, 10, 30, 30);

        jLabel_paystaionPackageBack2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionPackageBack2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageBack2MouseClicked(evt);
            }
        });
        passiveCardPayment.add(jLabel_paystaionPackageBack2);
        jLabel_paystaionPackageBack2.setBounds(620, 10, 30, 30);

        Page_Title60.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title60.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title60.setForeground(java.awt.Color.white);
        Page_Title60.setText("Lydia");
        passiveCardPayment.add(Page_Title60);
        Page_Title60.setBounds(20, 0, 360, 60);

        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel57.setText("Add");
        jLabel57.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel57MouseClicked(evt);
            }
        });
        passiveCardPayment.add(jLabel57);
        jLabel57.setBounds(0, 0, 760, 80);

        jLabel80.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        jLabel80.setForeground(java.awt.Color.white);
        jLabel80.setText("Pay");
        jLabel80.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel80MouseClicked(evt);
            }
        });
        passiveCardPayment.add(jLabel80);
        jLabel80.setBounds(190, 310, 70, 60);

        jLabel81.setFont(new java.awt.Font("DejaVu Sans", 0, 24)); // NOI18N
        jLabel81.setForeground(java.awt.Color.white);
        jLabel81.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/callout-red-bg (2).png"))); // NOI18N
        jLabel81.setText("Cancel");
        jLabel81.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel81MouseClicked(evt);
            }
        });
        passiveCardPayment.add(jLabel81);
        jLabel81.setBounds(350, 310, 240, 60);

        jLabel82.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue_48.png"))); // NOI18N
        passiveCardPayment.add(jLabel82);
        jLabel82.setBounds(110, 310, 50, 60);

        jLabel83.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/mdpi.png"))); // NOI18N
        passiveCardPayment.add(jLabel83);
        jLabel83.setBounds(360, 310, 60, 60);

        atm_outer.add(passiveCardPayment, "card2");

        Faq1.setBackground(new java.awt.Color(34, 47, 47));
        Faq1.setForeground(new java.awt.Color(0, 102, 102));
        Faq1.setLayout(null);

        Page_Title67.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title67.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title67.setForeground(java.awt.Color.white);
        Page_Title67.setText("Go to lydia.com to and login using username and passwod. then go to change packages");
        Faq1.add(Page_Title67);
        Page_Title67.setBounds(80, 380, 690, 30);

        jLabel_paystaionPackageHome3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionPackageHome3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionPackageHome3MouseClicked(evt);
            }
        });
        Faq1.add(jLabel_paystaionPackageHome3);
        jLabel_paystaionPackageHome3.setBounds(690, 20, 30, 30);

        Page_Title69.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title69.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title69.setForeground(java.awt.Color.white);
        Page_Title69.setText("Lydia");
        Faq1.add(Page_Title69);
        Page_Title69.setBounds(20, 0, 260, 60);

        jLabel90.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel90.setText("Add");
        jLabel90.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel90MouseClicked(evt);
            }
        });
        Faq1.add(jLabel90);
        jLabel90.setBounds(340, 430, 60, 50);

        jLabel86.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel86.setText("Add");
        jLabel86.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel86MouseClicked(evt);
            }
        });
        Faq1.add(jLabel86);
        jLabel86.setBounds(0, 0, 780, 80);

        Page_Title70.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title70.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        Page_Title70.setForeground(java.awt.Color.white);
        Page_Title70.setText("Frequently Ask Question");
        Faq1.add(Page_Title70);
        Page_Title70.setBounds(20, 90, 620, 60);

        Page_Title68.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title68.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title68.setForeground(java.awt.Color.white);
        Page_Title68.setText("2");
        Faq1.add(Page_Title68);
        Page_Title68.setBounds(360, 440, 20, 30);

        Page_Title72.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title72.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title72.setForeground(java.awt.Color.white);
        Page_Title72.setText("Q3 : How Can i Change My Packages");
        Faq1.add(Page_Title72);
        Page_Title72.setBounds(30, 340, 400, 30);

        Page_Title73.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title73.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title73.setForeground(java.awt.Color.white);
        Page_Title73.setText("Q1 : I dont remeber the pin number");
        Faq1.add(Page_Title73);
        Page_Title73.setBounds(30, 160, 400, 30);

        jLabel91.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/callout-red-bg (2).png"))); // NOI18N
        jLabel91.setText("Add");
        jLabel91.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel91MouseClicked(evt);
            }
        });
        Faq1.add(jLabel91);
        jLabel91.setBounds(260, 430, 60, 50);

        Page_Title74.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title74.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title74.setForeground(java.awt.Color.white);
        Page_Title74.setText("1");
        Faq1.add(Page_Title74);
        Page_Title74.setBounds(280, 440, 20, 30);

        Page_Title75.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title75.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title75.setForeground(java.awt.Color.white);
        Page_Title75.setText("Call Our Call Center To Recover Your Pin Number . Hotline 0777");
        Faq1.add(Page_Title75);
        Page_Title75.setBounds(80, 210, 520, 30);

        Page_Title76.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title76.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title76.setForeground(java.awt.Color.white);
        Page_Title76.setText("Q2 : My Card Is Damage");
        Faq1.add(Page_Title76);
        Page_Title76.setBounds(30, 250, 400, 30);

        Page_Title77.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title77.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title77.setForeground(java.awt.Color.white);
        Page_Title77.setText("Please go to nearest Lydia Travel Office to Get A New Card");
        Faq1.add(Page_Title77);
        Page_Title77.setBounds(80, 300, 520, 30);

        Page_Title89.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title89.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title89.setForeground(java.awt.Color.white);
        Page_Title89.setText("Home/FaQ/");
        Faq1.add(Page_Title89);
        Page_Title89.setBounds(0, 470, 690, 30);

        atm_outer.add(Faq1, "card2");

        Faq2.setBackground(new java.awt.Color(34, 47, 47));
        Faq2.setForeground(new java.awt.Color(0, 102, 102));
        Faq2.setLayout(null);

        Page_Title78.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title78.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title78.setForeground(java.awt.Color.white);
        Page_Title78.setText("Home/FaQ/");
        Faq2.add(Page_Title78);
        Page_Title78.setBounds(0, 470, 690, 30);

        jLabel84.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel84.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel84MouseClicked(evt);
            }
        });
        Faq2.add(jLabel84);
        jLabel84.setBounds(690, 20, 30, 30);

        Page_Title79.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title79.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title79.setForeground(java.awt.Color.white);
        Page_Title79.setText("Lydia");
        Faq2.add(Page_Title79);
        Page_Title79.setBounds(20, 0, 260, 60);

        jLabel92.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/callout-red-bg (2).png"))); // NOI18N
        jLabel92.setText("Add");
        jLabel92.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel92MouseClicked(evt);
            }
        });
        Faq2.add(jLabel92);
        jLabel92.setBounds(340, 430, 60, 50);

        jLabel87.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel87.setText("Add");
        jLabel87.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel87MouseClicked(evt);
            }
        });
        Faq2.add(jLabel87);
        jLabel87.setBounds(0, 0, 780, 80);

        Page_Title80.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title80.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        Page_Title80.setForeground(java.awt.Color.white);
        Page_Title80.setText("Frequently Ask Question");
        Faq2.add(Page_Title80);
        Page_Title80.setBounds(20, 90, 620, 60);

        Page_Title81.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title81.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title81.setForeground(java.awt.Color.white);
        Page_Title81.setText("2");
        Faq2.add(Page_Title81);
        Page_Title81.setBounds(360, 440, 20, 30);

        Page_Title82.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title82.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title82.setForeground(java.awt.Color.white);
        Page_Title82.setText("Q6 : How Can i Change My Packages");
        Faq2.add(Page_Title82);
        Page_Title82.setBounds(30, 340, 400, 30);

        Page_Title83.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title83.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title83.setForeground(java.awt.Color.white);
        Page_Title83.setText("Q4 : Where Can I Download Lydia Travel Mobile App");
        Faq2.add(Page_Title83);
        Page_Title83.setBounds(30, 160, 640, 30);

        jLabel93.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel93.setText("Add");
        jLabel93.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel93MouseClicked(evt);
            }
        });
        Faq2.add(jLabel93);
        jLabel93.setBounds(260, 430, 60, 50);

        Page_Title84.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title84.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title84.setForeground(java.awt.Color.white);
        Page_Title84.setText("1");
        Faq2.add(Page_Title84);
        Page_Title84.setBounds(280, 440, 20, 30);

        Page_Title85.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title85.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title85.setForeground(java.awt.Color.white);
        Page_Title85.setText("Please Visit Lydia.com or Search PlayStore or AppleStore ");
        Faq2.add(Page_Title85);
        Page_Title85.setBounds(80, 210, 520, 30);

        Page_Title86.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title86.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title86.setForeground(java.awt.Color.white);
        Page_Title86.setText("Q5 : How Can I find Nearest PayStation");
        Faq2.add(Page_Title86);
        Page_Title86.setBounds(30, 250, 400, 30);

        Page_Title87.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title87.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title87.setForeground(java.awt.Color.white);
        Page_Title87.setText("Send SMS with NP to 0777 .You will get nearest PayStation");
        Faq2.add(Page_Title87);
        Page_Title87.setBounds(80, 300, 520, 30);

        Page_Title88.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title88.setFont(new java.awt.Font("Ubuntu Light", 0, 18)); // NOI18N
        Page_Title88.setForeground(java.awt.Color.white);
        Page_Title88.setText("Go to lydia.com to and login using username and passwod. then go to change packages");
        Faq2.add(Page_Title88);
        Page_Title88.setBounds(80, 380, 690, 30);

        atm_outer.add(Faq2, "card2");

        card_pay1.setBackground(new java.awt.Color(38, 45, 45));
        card_pay1.setLayout(null);

        Page_Title90.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title90.setFont(new java.awt.Font("Ubuntu Light", 0, 27)); // NOI18N
        Page_Title90.setForeground(java.awt.Color.white);
        Page_Title90.setText("Pay By Card ");
        card_pay1.add(Page_Title90);
        Page_Title90.setBounds(40, 100, 200, 30);
        card_pay1.add(jLabel65CardPayAmount1);
        jLabel65CardPayAmount1.setBounds(390, 80, 130, 40);

        blue48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue1.png"))); // NOI18N
        card_pay1.add(blue48);
        blue48.setBounds(110, 600, 60, 25);

        blue49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue2.png"))); // NOI18N
        card_pay1.add(blue49);
        blue49.setBounds(170, 600, 60, 25);

        blue50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue3.png"))); // NOI18N
        card_pay1.add(blue50);
        blue50.setBounds(220, 600, 60, 25);

        blue51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue4.png"))); // NOI18N
        card_pay1.add(blue51);
        blue51.setBounds(280, 600, 60, 25);

        blue52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue5.png"))); // NOI18N
        card_pay1.add(blue52);
        blue52.setBounds(340, 600, 60, 25);

        gray48.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray1.png"))); // NOI18N
        card_pay1.add(gray48);
        gray48.setBounds(110, 600, 60, 25);

        gray49.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray2.png"))); // NOI18N
        card_pay1.add(gray49);
        gray49.setBounds(170, 600, 60, 25);

        gray50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray3.png"))); // NOI18N
        card_pay1.add(gray50);
        gray50.setBounds(220, 600, 60, 25);

        gray51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray4.png"))); // NOI18N
        card_pay1.add(gray51);
        gray51.setBounds(280, 600, 60, 25);

        gray52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/gray5.png"))); // NOI18N
        card_pay1.add(gray52);
        gray52.setBounds(340, 600, 60, 25);

        Page_Title91.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title91.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title91.setForeground(java.awt.Color.white);
        Page_Title91.setText("Card No");
        card_pay1.add(Page_Title91);
        Page_Title91.setBounds(170, 180, 100, 30);

        cardPayCardNo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cardPayCardNo1ActionPerformed(evt);
            }
        });
        card_pay1.add(cardPayCardNo1);
        cardPayCardNo1.setBounds(280, 180, 280, 40);

        cardPayError1.setBackground(new java.awt.Color(255, 51, 0));
        cardPayError1.setForeground(new java.awt.Color(255, 255, 255));
        cardPayError1.setText("Invalid Details");
        card_pay1.add(cardPayError1);
        cardPayError1.setBounds(290, 330, 200, 20);

        jLabel_paystaionCardPayHome3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/smalllogo.png"))); // NOI18N
        jLabel_paystaionCardPayHome3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayHome3MouseClicked(evt);
            }
        });
        card_pay1.add(jLabel_paystaionCardPayHome3);
        jLabel_paystaionCardPayHome3.setBounds(720, 20, 40, 30);

        jLabel_paystaionCardPayBack3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/small_gree.png"))); // NOI18N
        jLabel_paystaionCardPayBack3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayBack3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel_paystaionCardPayBack3MouseEntered(evt);
            }
        });
        card_pay1.add(jLabel_paystaionCardPayBack3);
        jLabel_paystaionCardPayBack3.setBounds(680, 20, 40, 30);

        paystationCardPayPin1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystationCardPayPin1ActionPerformed(evt);
            }
        });
        card_pay1.add(paystationCardPayPin1);
        paystationCardPayPin1.setBounds(280, 230, 280, 40);

        paystationCardAmount12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paystationCardAmount12ActionPerformed(evt);
            }
        });
        card_pay1.add(paystationCardAmount12);
        paystationCardAmount12.setBounds(280, 280, 280, 40);

        Page_Title97CardAmount.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title97CardAmount.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title97CardAmount.setForeground(java.awt.Color.white);
        Page_Title97CardAmount.setText("Amount");
        card_pay1.add(Page_Title97CardAmount);
        Page_Title97CardAmount.setBounds(180, 290, 110, 30);

        Page_Title92.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title92.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title92.setForeground(java.awt.Color.white);
        Page_Title92.setText("          Cancel The Payment");
        Page_Title92.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Page_Title92MouseClicked(evt);
            }
        });
        card_pay1.add(Page_Title92);
        Page_Title92.setBounds(420, 400, 280, 60);

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/callout-red-bg (2).png"))); // NOI18N
        jLabel18.setText("Add");
        jLabel18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel18MouseClicked(evt);
            }
        });
        card_pay1.add(jLabel18);
        jLabel18.setBounds(420, 400, 290, 60);

        jLabel95.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel95.setText("Add");
        jLabel95.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel95MouseClicked(evt);
            }
        });
        card_pay1.add(jLabel95);
        jLabel95.setBounds(0, 0, 790, 80);
        card_pay1.add(jLabel96);
        jLabel96.setBounds(280, 130, 200, 30);

        jLabel97.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/buttonBlue1.png"))); // NOI18N
        jLabel97.setText("Add");
        jLabel97.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel97MouseClicked(evt);
            }
        });
        card_pay1.add(jLabel97);
        jLabel97.setBounds(80, 400, 300, 60);

        Page_Title93.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title93.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title93.setForeground(java.awt.Color.white);
        Page_Title93.setText("Accept The Payment");
        card_pay1.add(Page_Title93);
        Page_Title93.setBounds(150, 410, 230, 30);

        Page_Title94.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title94.setFont(new java.awt.Font("Ubuntu Light", 0, 24)); // NOI18N
        Page_Title94.setForeground(java.awt.Color.white);
        Page_Title94.setText("PIN");
        card_pay1.add(Page_Title94);
        Page_Title94.setBounds(210, 240, 40, 30);

        Page_Title96.setBackground(new java.awt.Color(102, 102, 102));
        Page_Title96.setFont(new java.awt.Font("Ubuntu Light", 0, 36)); // NOI18N
        Page_Title96.setForeground(java.awt.Color.white);
        Page_Title96.setText("Lydia");
        card_pay1.add(Page_Title96);
        Page_Title96.setBounds(20, 0, 200, 60);
        card_pay1.add(jLabel98);
        jLabel98.setBounds(280, 130, 200, 30);

        jLabel99.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/blue_48.png"))); // NOI18N
        card_pay1.add(jLabel99);
        jLabel99.setBounds(90, 400, 50, 60);

        jLabel100.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resourses/mdpi.png"))); // NOI18N
        card_pay1.add(jLabel100);
        jLabel100.setBounds(420, 400, 48, 60);

        atm_outer.add(card_pay1, "card2");

        getContentPane().add(atm_outer);
        atm_outer.setBounds(0, 0, 780, 520);

        pack();
        setLocationRelativeTo(null);
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
        currentPassiveCard = false;
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(jPanel_selectSmartCradOptions);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel3MouseClicked

    private void paystationPayByCashMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paystationPayByCashMouseClicked

        paystationInvalidAmount.setVisible(false);
        if (currentPassiveCard == false) { //if it's a smartcard

            if (topUpAccount) { //topup
                // removing pane
                atm_outer.removeAll();
                atm_outer.repaint();
                atm_outer.revalidate();
                //adding pane
                atm_outer.add(cash_pay3);
                atm_outer.repaint();
            } else { //new smart card

                Page_Title21.setText("Total Amountt to pay " + String.valueOf(currentPackage.getAmountNeeded()));
                // removing pane
                atm_outer.removeAll();
                atm_outer.repaint();
                atm_outer.revalidate();
                //adding pane
                atm_outer.add(cash_pay);
                atm_outer.repaint();
            }

        } else //if it's a passive card
        {
            // removing pane
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(passiveCardPayment);
            atm_outer.repaint();

            passiveCardPayAmount.setText(String.valueOf(passiveChargeAmount));

        }

    }//GEN-LAST:event_paystationPayByCashMouseClicked

    private void paystationPayByCardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paystationPayByCardMouseClicked

        cardPayCardNo.setText("");
        paystationCardPayPin.setText("");
        cardPayError.setVisible(false);

        if (currentPassiveCard == false) { //if it's a smartcard

            if (topUpAccount) { //topup
                // removing pane
                atm_outer.removeAll();
                atm_outer.repaint();
                atm_outer.revalidate();
                //adding pane
                atm_outer.add(card_selet);
                atm_outer.repaint();
            } else { //new smart card

                Page_Title33.setText("Total Amount Rs " + String.valueOf(currentPackage.getAmountNeeded()));
                // removing pane
                atm_outer.removeAll();
                atm_outer.repaint();
                atm_outer.revalidate();
                //adding pane
                atm_outer.add(card_selet);
                atm_outer.repaint();
            }

        } else //if it's a passive card
        {
            // removing pane
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(card_selet);
            atm_outer.repaint();

            Page_Title33.setText("Total Amount Rs " + String.valueOf(passiveChargeAmount));

        }


    }//GEN-LAST:event_paystationPayByCardMouseClicked

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
        cardPayError1.setVisible(false);
        if (topUpAccount) { //topup
            // removing pane
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(card_pay1);
            atm_outer.repaint();
            atm_outer.revalidate();
        } else {
            // removing pane
            atm_outer.removeAll();
            atm_outer.repaint();
            atm_outer.revalidate();
            //adding pane
            atm_outer.add(card_pay);
            atm_outer.repaint();
            atm_outer.revalidate();
        }


    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked

        if ((paystationCardPayPin.getText().isEmpty()) || (cardPayCardNo.getText().isEmpty())) {

            cardPayError.setText("Enter credentials");
            cardPayError.setVisible(true);

        } else {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(this, "Your Message", "Title on Box", dialogButton);
            if (dialogResult == 0) {
                acceptPayment(currentPackage.getAmountNeeded());
            } else {
                System.out.println("No Option");
            }
        }

    }//GEN-LAST:event_jLabel13MouseClicked

    private void paystationCardPayPinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paystationCardPayPinActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paystationCardPayPinActionPerformed

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
        //initialize
        cardPayError.setVisible(false);
        //   cardPayAmount.setText("");
        cardPayCardNo.setText("");
        paystationCardPayPin.setText("");

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_Page_Title16MouseClicked

    private void Page_Title26MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Page_Title26MouseClicked
        jTextField_paystationCashAmount.setText("");
        paystationInvalidAmount.setVisible(false);
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentPassiveCard = false;
        passiveChargeAmount = 0;

        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_Page_Title26MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        
        currentPassiveCard = false;
        jTextField_paystationCashAmount.setText("");
        paystationInvalidAmount.setVisible(false);
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentPassiveCard = false;
        passiveChargeAmount = 0;

        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel27MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel27MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel27MouseClicked

    private void jLabel28MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel28MouseClicked

        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this, "Your Message", "Title on Box", dialogButton);
        if (dialogResult == 0) {
            acceptPayment(currentPackage.getAmountNeeded());
        } else {
            System.out.println("No Option");
        }


    }//GEN-LAST:event_jLabel28MouseClicked

    private void jLabel37MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel37MouseClicked
        // TODO add your handling code here:
        topUpAccount = false;
        gray6.setVisible(false);
        blue6.setVisible(true);
        //removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(enterCardPin);
        atm_outer.repaint();
        atm_outer.revalidate();


    }//GEN-LAST:event_jLabel37MouseClicked

    private void jLabel38MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel38MouseClicked

        topUpAccount = true;
        paystaionCardIdInput.setText("");
        paystaionCardPinTopUp1.setText("");
        paystaionCardIdInvalid.setVisible(false);

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(enterCardId);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel38MouseClicked

    private void jLabel39MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel39MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel39MouseClicked

    private void jLable_newSmartCardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLable_newSmartCardMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLable_newSmartCardMouseClicked

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
        currentPassiveCard = false;
        jTextField_paystationCashAmount.setText("");
        paystationInvalidAmount.setVisible(false);
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentPassiveCard = false;
        passiveChargeAmount = 0;

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
//        jTextField_paystationCashAmount.setText("");
        paystationInvalidAmount.setVisible(false);
//        currentSmartAcc = null;
//        currentPackage = null;
//        currentSmartCard = null;
//        currentPassiveCard = false;
//        passiveChargeAmount = 0;

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
        //initialize
        cardPayError.setVisible(false);
        cardPayCardNo.setText("");
        paystationCardPayPin.setText("");

        jTextField_paystationCashAmount.setText("");
        paystationInvalidAmount.setVisible(false);
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentPassiveCard = false;
        passiveChargeAmount = 0;

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
        //initialize
        cardPayError.setVisible(false);

        cardPayCardNo.setText("");
        paystationCardPayPin.setText("");
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(card_selet);
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
            blue18.setVisible(false);
            validateSmartCardId(paystaionCardIdInput.getText(), Integer.parseInt(paystaionCardPinTopUp1.getText()));

        } catch (NumberFormatException e) {
            paystaionCardIdInvalid.setVisible(true);
        }

        //System.out.println("card number sc acc no: "+currentSmartCard.getAcountNumber());
    }//GEN-LAST:event_paystaionCardIdEnterMouseClicked

    private void jLabel_paystaionCardPayHome2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayHome2MouseClicked
        // TODO add your handling code here:

        //initializing 
        paystaionCardPinInput1.setText("");
        paystaionCardPinInput2.setText("");
        paystaionCardPinNoMatch.setVisible(false);

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

        //initializing
        paystaionCardPinInput1.setText("");
        paystaionCardPinInput2.setText("");
        paystaionCardPinNoMatch.setVisible(false);

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
        if ((!paystaionCardPinInput1.getText().isEmpty()) && (!paystaionCardPinInput1.getText().isEmpty())) { //fill all fiedls

            try { //if details are correct
                validatePin(Integer.parseInt(paystaionCardPinInput1.getText()), Integer.parseInt(paystaionCardPinInput2.getText()));
                paystaionCardPinNoMatch.setVisible(false); //hide the error lable

            } catch (NumberFormatException e) {
                //show error message
                paystaionCardPinNoMatch.setVisible(true);
                paystaionCardPinNoMatch.setText("Only Numbers allowed");
            }
        } else {
            //show error message
            paystaionCardPinNoMatch.setVisible(true);
            paystaionCardPinNoMatch.setText("Fill all fields");
        }

        //clear fields
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

        passiveCardSourse.setSelectedIndex(0);
        passiveCardDestination.setSelectedIndex(0);
        passiveCardTotal.setText("");
        passiveCardNormalNext.setVisible(false);
        paystationPassiveNormalError.setVisible(false);

        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();

        currentPassiveCard = false;

    }//GEN-LAST:event_jLabel_paystaionPaymentHome1MouseClicked

    private void jLabel_paystaionPaymentBack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPaymentBack1MouseClicked
        // TODO add your handling code here:
        // removing pane
        passiveCardSourse.setSelectedIndex(0);
        passiveCardDestination.setSelectedIndex(0);
        passiveCardTotal.setText("");
        passiveCardNormalNext.setVisible(false);
        paystationPassiveNormalError.setVisible(false);

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

    private void jLabel_paystaionPackageHome1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageHome1MouseClicked
        // TODO add your handling code here:
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel_paystaionPackageHome1MouseClicked

    private void jLabel_paystaionPackageBack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageBack1MouseClicked
        // TODO add your handling code here:
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
        currentPassiveCard = false;
    }//GEN-LAST:event_jLabel_paystaionPackageBack1MouseClicked

    private void jLabel55MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel55MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel55MouseClicked

    private void jLabel_paystationDayPack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationDayPack1MouseClicked
        try {
            // TODO add your handling code here:
            selectPackage(p.get(1));
            issueCard(0);
        } catch (ParseException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(" passive package 2 :" + currentPackage.getOffPeakCharge());
        paystationNormalOffpeakOutput.setText(String.valueOf(currentPackage.getOffPeakCharge()));
        paystationNormalPeakOutput.setText(String.valueOf(currentPackage.getPeakCharge()));
        //normal package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(paystationPassiveCardNormal);
        atm_outer.repaint();
        atm_outer.revalidate();


    }//GEN-LAST:event_jLabel_paystationDayPack1MouseClicked

    private void jLabel_paystationNightPack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPack1MouseClicked
        try {
            // TODO add your handling code here:
            selectPackage(p.get(0));
            issueCard(0);
        } catch (ParseException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(" passive package 1 :" + currentPackage.getOffPeakCharge());

        paystationNormalOffpeakOutput.setText(String.valueOf(currentPackage.getOffPeakCharge()));
        paystationNormalPeakOutput.setText(String.valueOf(currentPackage.getPeakCharge()));
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
        if ((passiveCardSourse.getSelectedIndex() > 0) && (passiveCardDestination.getSelectedIndex() > 0)) {
            double distance = theDistanceChart.getDistance((String) passiveCardSourse.getSelectedItem(), (String) passiveCardDestination.getSelectedItem());
            passiveChargeAmount = calculateCharge(distance);
            passiveCardTotal.setText(String.valueOf(passiveChargeAmount));
            passiveCardNormalNext.setVisible(true);
            paystationPassiveNormalError.setVisible(false);
        } else {
            paystationPassiveNormalError.setVisible(true);
            passiveCardTotal.setText("-");
            passiveCardNormalNext.setVisible(false);
        }

    }//GEN-LAST:event_passivceCardNormalCheckMouseClicked

    private void passiveCardNormalNextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_passiveCardNormalNextMouseClicked
        // TODO add your handling code here:
        passiveCardSourse.setSelectedIndex(0);
        passiveCardDestination.setSelectedIndex(0);
        passiveCardTotal.setText("");
        passiveCardNormalNext.setVisible(false);
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_passiveCardNormalNextMouseClicked

    private void cardPayCardNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cardPayCardNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cardPayCardNoActionPerformed

    private void jLabel_paystaionCardPayBackMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayBackMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystaionCardPayBackMouseEntered

    private void jLabel_paystationNightPack2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPack2MouseClicked
        // TODO add your handling code here:

        try {
            // TODO add your handling code here:
            selectPackage(p.get(3));
            issueCard(0);
        } catch (ParseException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(" passive package 1 :" + currentPackage.getOffPeakCharge());

        paystationNormalOffpeakOutput.setText(String.valueOf(currentPackage.getOffPeakCharge()));
        paystationNormalPeakOutput.setText(String.valueOf(currentPackage.getPeakCharge()));
        //normal package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(paystationPassiveCardNormal);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystationNightPack2MouseClicked

    private void jLabel_paystationNightPack3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPack3MouseClicked
        // TODO add your handling code here:

        try {
            // TODO add your handling code here:
            selectPackage(p.get(2));
            issueCard(0);
        } catch (ParseException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PayStationUi.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(" passive package 1 :" + currentPackage.getOffPeakCharge());

        paystationNormalOffpeakOutput.setText(String.valueOf(currentPackage.getOffPeakCharge()));
        paystationNormalPeakOutput.setText(String.valueOf(currentPackage.getPeakCharge()));
        //normal package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(paystationPassiveCardNormal);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystationNightPack3MouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        // TODO add your handling code here:

        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this, "Your Message", "Title on Box", dialogButton);
        if (dialogResult == 0) {
            acceptPayment(passiveChargeAmount);
        } else {
            System.out.println("No Option");
        }
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jLabel_paystaionPackageHome2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageHome2MouseClicked
        // TODO add your handling code here:
        currentPassiveCard = false;
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel_paystaionPackageHome2MouseClicked

    private void jLabel_paystaionPackageBack2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageBack2MouseClicked
        // TODO add your handling code here:
        
         atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionPackageBack2MouseClicked

    private void jLabel57MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel57MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel57MouseClicked

    private void jLabel64MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel64MouseClicked
        // TODO add your handling code here:

        //  paystationInvalidAmount.setVisible(false);
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentPassiveCard = false;
        passiveChargeAmount = 0;

        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel64MouseClicked

    private void jLabel_paystaionCashPayHome3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCashPayHome3MouseClicked

        jTextField_paystationCashAmount.setText("");
        paystationInvalidAmount.setVisible(false);
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentPassiveCard = false;
        passiveChargeAmount = 0;

        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();

    }//GEN-LAST:event_jLabel_paystaionCashPayHome3MouseClicked

    private void jLabel_paystaionCashPayBack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCashPayBack1MouseClicked
        // TODO add your handling code here:
        jTextField_paystationCashAmount.setText("");
        paystationInvalidAmount.setVisible(false);
//        currentSmartAcc = null;
//        currentPackage = null;
//        currentSmartCard = null;
//        currentPassiveCard = false;
//        passiveChargeAmount = 0;

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionCashPayBack1MouseClicked

    private void jTextField_paystationCashAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_paystationCashAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_paystationCashAmountActionPerformed

    private void Page_Title53MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Page_Title53MouseClicked
        jTextField_paystationCashAmount.setText("");
        paystationInvalidAmount.setVisible(false);
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentPassiveCard = false;
        passiveChargeAmount = 0;

        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_Page_Title53MouseClicked

    private void jLabel65MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel65MouseClicked
        
        jTextField_paystationCashAmount.setText("");
        paystationInvalidAmount.setVisible(false);
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentPassiveCard = false;
        passiveChargeAmount = 0;

        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel65MouseClicked

    private void jLabel66MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel66MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel66MouseClicked

    private void jLabel68MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel68MouseClicked

        String regex = "[0-9]+";
        if (jTextField_paystationCashAmount.getText().matches(regex)) {

            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(this, "Your Message", "Title on Box", dialogButton);
            if (dialogResult == 0) {

                acceptPayment(Double.parseDouble(jTextField_paystationCashAmount.getText()));
                paystationInvalidAmount.setVisible(false);
            } else {
                System.out.println("No Option");
            }

        } else {
            paystationInvalidAmount.setVisible(true);
        }

        jTextField_paystationCashAmount.setText("");

        //         paystaionCardIdInput.setText("");
        //        paystaionCardPinTopUp1.setText("");
    }//GEN-LAST:event_jLabel68MouseClicked

    private void jLabel71MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel71MouseClicked
        // TODO add your handling code here:
        currentPassiveCard = false;
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel71MouseClicked

    private void paystationPayByCard1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paystationPayByCard1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_paystationPayByCard1MouseClicked

    private void paystationPayByCash1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paystationPayByCash1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_paystationPayByCash1MouseClicked

    private void jLabel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel21MouseClicked

    private void jLabel29MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel29MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel29MouseClicked

    private void jLabel35MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel35MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel35MouseClicked

    private void jLabel48MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel48MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel48MouseClicked

    private void jLabel53MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel53MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel53MouseClicked

    private void passiveCardDestinationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passiveCardDestinationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passiveCardDestinationActionPerformed

    private void jLabel69MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel69MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel69MouseClicked

    private void jLabel73MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel73MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel73MouseClicked

    private void jLabel74MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel74MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel74MouseClicked

    private void jLabel80MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel80MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel80MouseClicked

    private void jLabel81MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel81MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel81MouseClicked

    private void jLabel86MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel86MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel86MouseClicked

    private void jLabel90MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel90MouseClicked
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(Faq2);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel90MouseClicked

    private void Page_Title71MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Page_Title71MouseClicked
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(Faq1);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_Page_Title71MouseClicked

    private void jLabel91MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel91MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel91MouseClicked

    private void jLabel92MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel92MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel92MouseClicked

    private void jLabel87MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel87MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel87MouseClicked

    private void jLabel93MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel93MouseClicked
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(Faq1);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel93MouseClicked

    private void jLabel_paystaionPackageHome3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageHome3MouseClicked
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionPackageHome3MouseClicked

    private void jLabel84MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel84MouseClicked
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel84MouseClicked

    private void jLabel_paystationDayPackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationDayPackMouseClicked

        selectPackage(p.get(1));//select the package
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

    private void jLabel34MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel34MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel34MouseClicked

    private void jLabel_paystationNightPackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPackMouseClicked

        selectPackage(p.get(0));//select the package

        // paymentOptionBreadcrumb.setText("Home -> smart card ->new smart card -> package -> Payment option");
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystationNightPackMouseClicked

    private void jLabel_paystationBudjectPackMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationBudjectPackMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationBudjectPackMouseEntered

    private void jLabel_paystationBudjectPackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationBudjectPackMouseClicked

        selectPackage(p.get(3));//select the package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystationBudjectPackMouseClicked

    private void jLabel_paystationMegaPackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationMegaPackMouseClicked

        selectPackage(p.get(3));//select the package
        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_payment_selct);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystationMegaPackMouseClicked

    private void jLabel_paystaionPackageBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionPackageBackMouseClicked
        // TODO add your handling code here:
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(jPanel_selectSmartCradOptions);
        atm_outer.repaint();
        atm_outer.revalidate();

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

    private void cardPayCardNo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cardPayCardNo1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cardPayCardNo1ActionPerformed

    private void jLabel_paystaionCardPayHome3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayHome3MouseClicked
        // TODO add your handling code here:
        currentSmartAcc = null;
        currentPackage = null;
        currentSmartCard = null;
        currentPassiveCard = false;
        passiveChargeAmount = 0;

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionCardPayHome3MouseClicked

    private void jLabel_paystaionCardPayBack3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayBack3MouseClicked
        // TODO add your handling code here:
//        currentSmartAcc = null;
//        currentPackage = null;
//        currentSmartCard = null;
//        currentPassiveCard = false;
//        passiveChargeAmount = 0;

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(card_selet);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_jLabel_paystaionCardPayBack3MouseClicked

    private void jLabel_paystaionCardPayBack3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystaionCardPayBack3MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystaionCardPayBack3MouseEntered

    private void paystationCardPayPin1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paystationCardPayPin1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paystationCardPayPin1ActionPerformed

    private void Page_Title92MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Page_Title92MouseClicked
        // TODO add your handling code here:

        //initialize
        cardPayError1.setVisible(false);
        //   cardPayAmount.setText("");
        cardPayCardNo1.setText("");
        paystationCardPayPin1.setText("");
        paystationCardAmount12.setText("");

        // removing pane
        atm_outer.removeAll();
        atm_outer.repaint();
        atm_outer.revalidate();
        //adding pane
        atm_outer.add(atm_home);
        atm_outer.repaint();
        atm_outer.revalidate();
    }//GEN-LAST:event_Page_Title92MouseClicked

    private void jLabel18MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel18MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel18MouseClicked

    private void jLabel95MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel95MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel95MouseClicked

    private void jLabel97MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel97MouseClicked
        // TODO add your handling code here:

        // acceptPayment(Double.parseDouble(paystationCardAmount.getText()));
        if ((cardPayCardNo1.getText().isEmpty()) || (paystationCardPayPin1.getText().isEmpty()) || (paystationCardAmount12.getText().isEmpty())) {

            cardPayError1.setVisible(true);

        } else {

            String regex = "[0-9]+";
            if (paystationCardAmount12.getText().matches(regex)) {

                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog(this, "Your Message", "Title on Box", dialogButton);
                if (dialogResult == 0) {

                    acceptPayment(Double.parseDouble(paystationCardAmount12.getText()));
                    paystationCardAmount12.setText("");
                    cardPayError1.setVisible(false);

                } else {
                    System.out.println("No Option");
                }

            } else {
                cardPayError1.setVisible(true);
            }

        }
    }//GEN-LAST:event_jLabel97MouseClicked

    private void paystationCardAmount12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paystationCardAmount12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_paystationCardAmount12ActionPerformed

    private void passiveCardSourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passiveCardSourseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passiveCardSourseActionPerformed

    private void jLabel_paystationNightPack4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPack4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationNightPack4MouseClicked

    private void jLabel_paystationDayPack2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationDayPack2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationDayPack2MouseClicked

    private void jLabel_paystationBudjectPack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationBudjectPack1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationBudjectPack1MouseClicked

    private void jLabel_paystationBudjectPack1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationBudjectPack1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationBudjectPack1MouseEntered

    private void jLabel_paystationMegaPack1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationMegaPack1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationMegaPack1MouseClicked

    private void jLabel_paystationNightPack5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPack5MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationNightPack5MouseClicked

    private void jLabel_paystationDayPack3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationDayPack3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationDayPack3MouseClicked

    private void jLabel_paystationNightPack6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPack6MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationNightPack6MouseClicked

    private void jLabel_paystationNightPack7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_paystationNightPack7MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel_paystationNightPack7MouseClicked

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
    private javax.swing.JPanel Faq1;
    private javax.swing.JPanel Faq2;
    private javax.swing.JLabel Hariya;
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
    private javax.swing.JLabel Page_Title23;
    private javax.swing.JLabel Page_Title24;
    private javax.swing.JLabel Page_Title25;
    private javax.swing.JLabel Page_Title26;
    private javax.swing.JLabel Page_Title27;
    private javax.swing.JLabel Page_Title28;
    private javax.swing.JLabel Page_Title29;
    private javax.swing.JLabel Page_Title32;
    private javax.swing.JLabel Page_Title33;
    private javax.swing.JLabel Page_Title34;
    private javax.swing.JLabel Page_Title35;
    private javax.swing.JLabel Page_Title36;
    private javax.swing.JLabel Page_Title37;
    private javax.swing.JLabel Page_Title39;
    private javax.swing.JLabel Page_Title40;
    private javax.swing.JLabel Page_Title41;
    private javax.swing.JLabel Page_Title42;
    private javax.swing.JLabel Page_Title43;
    private javax.swing.JLabel Page_Title44;
    private javax.swing.JLabel Page_Title45;
    private javax.swing.JLabel Page_Title47;
    private javax.swing.JLabel Page_Title48;
    private javax.swing.JLabel Page_Title49;
    private javax.swing.JLabel Page_Title5;
    private javax.swing.JLabel Page_Title50;
    private javax.swing.JLabel Page_Title51;
    private javax.swing.JLabel Page_Title52;
    private javax.swing.JLabel Page_Title53;
    private javax.swing.JLabel Page_Title54;
    private javax.swing.JLabel Page_Title55;
    private javax.swing.JLabel Page_Title56;
    private javax.swing.JLabel Page_Title57;
    private javax.swing.JLabel Page_Title58;
    private javax.swing.JLabel Page_Title59;
    private javax.swing.JLabel Page_Title6;
    private javax.swing.JLabel Page_Title60;
    private javax.swing.JLabel Page_Title61;
    private javax.swing.JLabel Page_Title62;
    private javax.swing.JLabel Page_Title63;
    private javax.swing.JLabel Page_Title64;
    private javax.swing.JLabel Page_Title65;
    private javax.swing.JLabel Page_Title67;
    private javax.swing.JLabel Page_Title68;
    private javax.swing.JLabel Page_Title69;
    private javax.swing.JLabel Page_Title7;
    private javax.swing.JLabel Page_Title70;
    private javax.swing.JLabel Page_Title71;
    private javax.swing.JLabel Page_Title72;
    private javax.swing.JLabel Page_Title73;
    private javax.swing.JLabel Page_Title74;
    private javax.swing.JLabel Page_Title75;
    private javax.swing.JLabel Page_Title76;
    private javax.swing.JLabel Page_Title77;
    private javax.swing.JLabel Page_Title78;
    private javax.swing.JLabel Page_Title79;
    private javax.swing.JLabel Page_Title8;
    private javax.swing.JLabel Page_Title80;
    private javax.swing.JLabel Page_Title81;
    private javax.swing.JLabel Page_Title82;
    private javax.swing.JLabel Page_Title83;
    private javax.swing.JLabel Page_Title84;
    private javax.swing.JLabel Page_Title85;
    private javax.swing.JLabel Page_Title86;
    private javax.swing.JLabel Page_Title87;
    private javax.swing.JLabel Page_Title88;
    private javax.swing.JLabel Page_Title89;
    private javax.swing.JLabel Page_Title9;
    private javax.swing.JLabel Page_Title90;
    private javax.swing.JLabel Page_Title91;
    private javax.swing.JLabel Page_Title92;
    private javax.swing.JLabel Page_Title93;
    private javax.swing.JLabel Page_Title94;
    private javax.swing.JLabel Page_Title96;
    private javax.swing.JLabel Page_Title97CardAmount;
    private javax.swing.JPanel atm_home;
    private javax.swing.JPanel atm_outer;
    private javax.swing.JPanel atm_payment_selct;
    private javax.swing.JLabel blue1;
    private javax.swing.JLabel blue11;
    private javax.swing.JLabel blue12;
    private javax.swing.JLabel blue13;
    private javax.swing.JLabel blue14;
    private javax.swing.JLabel blue16;
    private javax.swing.JLabel blue17;
    private javax.swing.JLabel blue18;
    private javax.swing.JLabel blue19;
    private javax.swing.JLabel blue2;
    private javax.swing.JLabel blue21;
    private javax.swing.JLabel blue22;
    private javax.swing.JLabel blue23;
    private javax.swing.JLabel blue24;
    private javax.swing.JLabel blue25;
    private javax.swing.JLabel blue26;
    private javax.swing.JLabel blue27;
    private javax.swing.JLabel blue28;
    private javax.swing.JLabel blue29;
    private javax.swing.JLabel blue3;
    private javax.swing.JLabel blue30;
    private javax.swing.JLabel blue31;
    private javax.swing.JLabel blue32;
    private javax.swing.JLabel blue33;
    private javax.swing.JLabel blue34;
    private javax.swing.JLabel blue35;
    private javax.swing.JLabel blue36;
    private javax.swing.JLabel blue37;
    private javax.swing.JLabel blue38;
    private javax.swing.JLabel blue39;
    private javax.swing.JLabel blue4;
    private javax.swing.JLabel blue40;
    private javax.swing.JLabel blue41;
    private javax.swing.JLabel blue42;
    private javax.swing.JLabel blue43;
    private javax.swing.JLabel blue44;
    private javax.swing.JLabel blue45;
    private javax.swing.JLabel blue46;
    private javax.swing.JLabel blue47;
    private javax.swing.JLabel blue48;
    private javax.swing.JLabel blue49;
    private javax.swing.JLabel blue5;
    private javax.swing.JLabel blue50;
    private javax.swing.JLabel blue51;
    private javax.swing.JLabel blue52;
    private javax.swing.JLabel blue6;
    private javax.swing.JLabel blue7;
    private javax.swing.JLabel blue8;
    private javax.swing.JLabel blue9;
    private javax.swing.JTextField cardPayCardNo;
    private javax.swing.JTextField cardPayCardNo1;
    private javax.swing.JLabel cardPayError;
    private javax.swing.JLabel cardPayError1;
    private javax.swing.JPanel card_paid;
    private javax.swing.JPanel card_pay;
    private javax.swing.JPanel card_pay1;
    private javax.swing.JPanel card_selet;
    private javax.swing.JPanel cash_pay;
    private javax.swing.JPanel cash_pay3;
    private javax.swing.JPanel enterCardId;
    private javax.swing.JPanel enterCardPin;
    private javax.swing.JLabel gray1;
    private javax.swing.JLabel gray11;
    private javax.swing.JLabel gray12;
    private javax.swing.JLabel gray13;
    private javax.swing.JLabel gray14;
    private javax.swing.JLabel gray16;
    private javax.swing.JLabel gray17;
    private javax.swing.JLabel gray18;
    private javax.swing.JLabel gray19;
    private javax.swing.JLabel gray2;
    private javax.swing.JLabel gray21;
    private javax.swing.JLabel gray22;
    private javax.swing.JLabel gray23;
    private javax.swing.JLabel gray24;
    private javax.swing.JLabel gray25;
    private javax.swing.JLabel gray26;
    private javax.swing.JLabel gray27;
    private javax.swing.JLabel gray28;
    private javax.swing.JLabel gray29;
    private javax.swing.JLabel gray3;
    private javax.swing.JLabel gray30;
    private javax.swing.JLabel gray31;
    private javax.swing.JLabel gray32;
    private javax.swing.JLabel gray33;
    private javax.swing.JLabel gray34;
    private javax.swing.JLabel gray35;
    private javax.swing.JLabel gray36;
    private javax.swing.JLabel gray37;
    private javax.swing.JLabel gray38;
    private javax.swing.JLabel gray39;
    private javax.swing.JLabel gray4;
    private javax.swing.JLabel gray40;
    private javax.swing.JLabel gray41;
    private javax.swing.JLabel gray42;
    private javax.swing.JLabel gray43;
    private javax.swing.JLabel gray44;
    private javax.swing.JLabel gray45;
    private javax.swing.JLabel gray46;
    private javax.swing.JLabel gray47;
    private javax.swing.JLabel gray48;
    private javax.swing.JLabel gray49;
    private javax.swing.JLabel gray5;
    private javax.swing.JLabel gray50;
    private javax.swing.JLabel gray51;
    private javax.swing.JLabel gray52;
    private javax.swing.JLabel gray6;
    private javax.swing.JLabel gray7;
    private javax.swing.JLabel gray8;
    private javax.swing.JLabel gray9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
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
    private javax.swing.JLabel jLabel31Offpeak;
    private javax.swing.JLabel jLabel31Offpeak1;
    private javax.swing.JLabel jLabel31Offpeak2;
    private javax.swing.JLabel jLabel31Offpeak3;
    private javax.swing.JLabel jLabel31OffpeakAmount;
    private javax.swing.JLabel jLabel31OffpeakAmount1;
    private javax.swing.JLabel jLabel31OffpeakAmount2;
    private javax.swing.JLabel jLabel31OffpeakAmount3;
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
    private javax.swing.JLabel jLabel46Peak;
    private javax.swing.JLabel jLabel46Peak1;
    private javax.swing.JLabel jLabel46Peak2;
    private javax.swing.JLabel jLabel46Peak3;
    private javax.swing.JLabel jLabel46Peak4;
    private javax.swing.JLabel jLabel46Peak5;
    private javax.swing.JLabel jLabel46Peak6;
    private javax.swing.JLabel jLabel46Peak7;
    private javax.swing.JLabel jLabel46PeakAmount;
    private javax.swing.JLabel jLabel46PeakAmount1;
    private javax.swing.JLabel jLabel46PeakAmount2;
    private javax.swing.JLabel jLabel46PeakAmount3;
    private javax.swing.JLabel jLabel46PeakAmountNeed;
    private javax.swing.JLabel jLabel46PeakAmountNeed1;
    private javax.swing.JLabel jLabel46PeakAmountNeed2;
    private javax.swing.JLabel jLabel46PeakAmountNeed3;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel52OffpeakAmount;
    private javax.swing.JLabel jLabel52OffpeakAmount1;
    private javax.swing.JLabel jLabel52OffpeakAmount2;
    private javax.swing.JLabel jLabel52OffpeakAmount3;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel59PeakAmount;
    private javax.swing.JLabel jLabel59PeakAmount1;
    private javax.swing.JLabel jLabel59PeakAmount2;
    private javax.swing.JLabel jLabel59PeakAmount3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel65CardPayAmount;
    private javax.swing.JLabel jLabel65CardPayAmount1;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel88AmountNeed;
    private javax.swing.JLabel jLabel88AmountNeed1;
    private javax.swing.JLabel jLabel88AmountNeed2;
    private javax.swing.JLabel jLabel88AmountNeed3;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JLabel jLabel_payStationBack;
    private javax.swing.JLabel jLabel_paystaionCardPayBack;
    private javax.swing.JLabel jLabel_paystaionCardPayBack1;
    private javax.swing.JLabel jLabel_paystaionCardPayBack2;
    private javax.swing.JLabel jLabel_paystaionCardPayBack3;
    private javax.swing.JLabel jLabel_paystaionCardPayHome;
    private javax.swing.JLabel jLabel_paystaionCardPayHome1;
    private javax.swing.JLabel jLabel_paystaionCardPayHome2;
    private javax.swing.JLabel jLabel_paystaionCardPayHome3;
    private javax.swing.JLabel jLabel_paystaionCardSelectBack;
    private javax.swing.JLabel jLabel_paystaionCardSelectHome;
    private javax.swing.JLabel jLabel_paystaionCashPayBack;
    private javax.swing.JLabel jLabel_paystaionCashPayBack1;
    private javax.swing.JLabel jLabel_paystaionCashPayHome;
    private javax.swing.JLabel jLabel_paystaionCashPayHome3;
    private javax.swing.JLabel jLabel_paystaionPackageBack;
    private javax.swing.JLabel jLabel_paystaionPackageBack1;
    private javax.swing.JLabel jLabel_paystaionPackageBack2;
    private javax.swing.JLabel jLabel_paystaionPackageHome;
    private javax.swing.JLabel jLabel_paystaionPackageHome1;
    private javax.swing.JLabel jLabel_paystaionPackageHome2;
    private javax.swing.JLabel jLabel_paystaionPackageHome3;
    private javax.swing.JLabel jLabel_paystaionPaymentBack;
    private javax.swing.JLabel jLabel_paystaionPaymentBack1;
    private javax.swing.JLabel jLabel_paystaionPaymentHome;
    private javax.swing.JLabel jLabel_paystaionPaymentHome1;
    private javax.swing.JLabel jLabel_paystaionSmartCardOptionBack;
    private javax.swing.JLabel jLabel_paystaionSmartCardOptionHome;
    private javax.swing.JLabel jLabel_paystationBudjectPack;
    private javax.swing.JLabel jLabel_paystationBudjectPack1;
    private javax.swing.JLabel jLabel_paystationDayPack;
    private javax.swing.JLabel jLabel_paystationDayPack1;
    private javax.swing.JLabel jLabel_paystationDayPack2;
    private javax.swing.JLabel jLabel_paystationDayPack3;
    private javax.swing.JLabel jLabel_paystationMegaPack;
    private javax.swing.JLabel jLabel_paystationMegaPack1;
    public static javax.swing.JLabel jLabel_paystationNightPack;
    private javax.swing.JLabel jLabel_paystationNightPack1;
    private javax.swing.JLabel jLabel_paystationNightPack2;
    private javax.swing.JLabel jLabel_paystationNightPack3;
    public static javax.swing.JLabel jLabel_paystationNightPack4;
    private javax.swing.JLabel jLabel_paystationNightPack5;
    private javax.swing.JLabel jLabel_paystationNightPack6;
    private javax.swing.JLabel jLabel_paystationNightPack7;
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
    private javax.swing.JTextField jTextField_paystationCashAmount;
    private javax.swing.JLabel journeyAmount1;
    private javax.swing.JLabel journeyAmount2;
    private javax.swing.JLabel passivceCardNormalCheck;
    private javax.swing.JComboBox passiveCardDestination;
    private javax.swing.JLabel passiveCardNormalNext;
    private javax.swing.JPanel passiveCardPaid;
    private javax.swing.JLabel passiveCardPayAmount;
    private javax.swing.JPanel passiveCardPayment;
    private javax.swing.JComboBox passiveCardSourse;
    private javax.swing.JLabel passiveCardTotal;
    private javax.swing.JPanel passivePackages;
    private javax.swing.JLabel paystaionCardId;
    private javax.swing.JLabel paystaionCardId1;
    private javax.swing.JLabel paystaionCardId2;
    private javax.swing.JLabel paystaionCardId3;
    private javax.swing.JLabel paystaionCardId4;
    private javax.swing.JLabel paystaionCardId5;
    private javax.swing.JLabel paystaionCardIdEnter;
    private javax.swing.JLabel paystaionCardIdEnterText1;
    private javax.swing.JTextField paystaionCardIdInput;
    private javax.swing.JLabel paystaionCardIdInvalid;
    private javax.swing.JTextField paystaionCardPinInput1;
    private javax.swing.JTextField paystaionCardPinInput2;
    private javax.swing.JLabel paystaionCardPinNoMatch;
    private javax.swing.JLabel paystaionCardPinSubmit;
    private javax.swing.JLabel paystaionCardPinTopUp;
    private javax.swing.JTextField paystaionCardPinTopUp1;
    private javax.swing.JLabel paystaionCardPinTopUp2;
    private javax.swing.JTextField paystationCardAmount12;
    private javax.swing.JTextField paystationCardPayPin;
    private javax.swing.JTextField paystationCardPayPin1;
    private javax.swing.JLabel paystationInvalidAmount;
    private javax.swing.JLabel paystationNormalOffpeak;
    private javax.swing.JLabel paystationNormalOffpeakOutput;
    private javax.swing.JLabel paystationNormalPeak;
    private javax.swing.JLabel paystationNormalPeakOutput;
    private javax.swing.JPanel paystationPassiveCardNormal;
    private javax.swing.JLabel paystationPassiveNormalError;
    private javax.swing.JLabel paystationPayByCard;
    private javax.swing.JLabel paystationPayByCard1;
    private javax.swing.JLabel paystationPayByCash;
    private javax.swing.JLabel paystationPayByCash1;
    private javax.swing.JPanel select_package;
    // End of variables declaration//GEN-END:variables
}
