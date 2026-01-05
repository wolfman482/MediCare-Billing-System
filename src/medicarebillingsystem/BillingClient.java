package medicarebillingsystem;

import java.io.*;
import java.net.*;
import java.util.*;

public class BillingClient implements Runnable {
    
    public void run() {
        try {
            Socket s = new Socket("localhost", 5000);
            
            Scanner sc = new Scanner(System.in);
            
            // Get input from user
            System.out.print("Enter Patient ID: ");
            int patientId = sc.nextInt();
            sc.nextLine(); // consume newline
            
            System.out.print("Enter Visit Date (YYYY-MM-DD): ");
            String visitDate = sc.nextLine();
            
            System.out.print("Enter Patient Type (Outpatient/Inpatient/Emergency): ");
            String patientType = sc.nextLine();
            
            System.out.print("Enter Service Code (CONS100/LAB210/IMG330/US400/MRI700): ");
            String serviceCode = sc.nextLine();
            
            // Send data to server
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeInt(patientId);
            dos.writeUTF(visitDate);
            dos.writeUTF(patientType);
            dos.writeUTF(serviceCode);
            dos.flush();
            
            // Receive data from server
            DataInputStream dis = new DataInputStream(s.getInputStream());
            
            String patientName = dis.readUTF();
            String insurancePlan = dis.readUTF();
            double baseFee = dis.readDouble();
            double insuranceDiscount = dis.readDouble();
            double perVisitDiscount = dis.readDouble();
            double discountedAmount = dis.readDouble();
            double extraCharge = dis.readDouble();
            double finalBill = dis.readDouble();
            
            // Display billing summary
            System.out.println("\n========== BILLING SUMMARY ==========");
            System.out.println("Patient ID: " + patientId);
            System.out.println("Patient Name: " + patientName);
            System.out.println("Visit Date: " + visitDate);
            System.out.println("Patient Type: " + patientType);
            System.out.println("Insurance Plan: " + insurancePlan);
            System.out.println("\n--- Billing Breakdown ---");
            System.out.println("Base Fee: " + baseFee + " OMR");
            System.out.println("Insurance Discount: -" + insuranceDiscount + " OMR");
            System.out.println("Per-Visit Discount: -" + perVisitDiscount + " OMR");
            System.out.println("Amount after Discounts: " + discountedAmount + " OMR");
            System.out.println("Extra Charge (" + patientType + "): +" + extraCharge + " OMR");
            System.out.println("FINAL BILL: " + finalBill + " OMR");
            System.out.println("====================================");
            
            dis.close();
            dos.close();
            s.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        Thread t = new Thread(new BillingClient());
        t.start();
    }
}