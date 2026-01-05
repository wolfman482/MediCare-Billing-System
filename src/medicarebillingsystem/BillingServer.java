package medicarebillingsystem;

import java.io.*;
import java.net.*;
import java.sql.*;

public class BillingServer implements Runnable {
    
    public void run() {
        try {
            // JDBC connection
            String url = "jdbc:derby://localhost:1527/medicaredb";
            String user = "medicare";
            String pass = "medicare123";
            
            Connection con = DriverManager.getConnection(url, user, pass);
            
            // Set schema to APP
            Statement schemaStmt = con.createStatement();
            schemaStmt.execute("SET SCHEMA APP");
            schemaStmt.close();
            
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Server started... waiting for client");
            
            
            Socket s = ss.accept();
            System.out.println("Client connected");
            
            // Receive data from client
            DataInputStream dis = new DataInputStream(s.getInputStream());
            int patientId = dis.readInt();
            String visitDate = dis.readUTF();
            String patientType = dis.readUTF();
            String serviceCode = dis.readUTF();
            
            System.out.println("Received - Patient ID: " + patientId);
            
            // Query database for patient info
            String query = "SELECT name, insurance_plan FROM Patient WHERE patient_id = " + patientId;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            
            if (rs.next()) {
                String patientName = rs.getString("name");
                String insurancePlan = rs.getString("insurance_plan");
                
                // Calculate billing
                double baseFee = getBaseFee(serviceCode);
                double insuranceDiscount = getInsuranceDiscount(baseFee, insurancePlan);
                double perVisitDiscount = getPerVisitDiscount(insurancePlan);
                double discountedAmount = baseFee - insuranceDiscount - perVisitDiscount;
                double extraCharge = getExtraCharge(discountedAmount, patientType);
                double finalBill = discountedAmount + extraCharge;
                
                System.out.println("Calculated bill: " + finalBill + " OMR");
                
                // Insert into PatientBill
                String insertQuery = "INSERT INTO PatientBill (patient_id, visit_date, bill_amount) VALUES (?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(insertQuery);
                pstmt.setInt(1, patientId);
                pstmt.setDate(2, java.sql.Date.valueOf(visitDate));
                pstmt.setDouble(3, finalBill);
                pstmt.executeUpdate();
                pstmt.close();
                
                // Send data to client
                dos.writeUTF(patientName);
                dos.writeUTF(insurancePlan);
                dos.writeDouble(baseFee);
                dos.writeDouble(insuranceDiscount);
                dos.writeDouble(perVisitDiscount);
                dos.writeDouble(discountedAmount);
                dos.writeDouble(extraCharge);
                dos.writeDouble(finalBill);
                
            } else {
                dos.writeUTF("NOT FOUND");
                dos.writeUTF("");
                dos.writeDouble(0);
                dos.writeDouble(0);
                dos.writeDouble(0);
                dos.writeDouble(0);
                dos.writeDouble(0);
                dos.writeDouble(0);
            }
            
            dos.close();
            dis.close();
            s.close();
            ss.close();
            con.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Helper methods for billing calculation
    private double getBaseFee(String serviceCode) {
        switch (serviceCode) {
            case "CONS100": return 12.00;
            case "LAB210": return 8.50;
            case "IMG330": return 25.00;
            case "US400": return 35.00;
            case "MRI700": return 180.00;
            default: return 0.00;
        }
    }
    
    private double getInsuranceDiscount(double baseFee, String plan) {
        switch (plan) {
            case "Premium": return baseFee * 0.15;
            case "Standard": return baseFee * 0.10;
            case "Basic": return baseFee * 0.00;
            default: return 0.00;
        }
    }
    
    private double getPerVisitDiscount(String plan) {
        switch (plan) {
            case "Premium": return 5.00;
            case "Standard": return 8.00;
            case "Basic": return 10.00;
            default: return 0.00;
        }
    }
    
    private double getExtraCharge(double amount, String type) {
        switch (type) {
            case "Inpatient": return amount * 0.05;
            case "Emergency": return amount * 0.15;
            default: return 0.00;
        }
    }
    
    public static void main(String[] args) {
        Thread t = new Thread(new BillingServer());
        t.start();
    }
}