package medicarebillingsystem;

import java.util.*;



public class PatientBillingUtility {
    
    // HashMap to store billing records: Key = Patient ID, Value = BillingRecord
    private static HashMap<Integer, BillingRecord> billingRecords = new HashMap<>();
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;
        
        do {
            // Display menu
            System.out.println("\n========================================");
            System.out.println("   PATIENT BILLING UTILITY SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Add Billing Record");
            System.out.println("2. Display Billing Record");
            System.out.println("3. Remove Billing Record");
            System.out.println("4. Display All Records");
            System.out.println("5. Exit");
            System.out.println("========================================");
            System.out.print("Enter your choice (1-5): ");
            
            choice = sc.nextInt();
            sc.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    addBillingRecord(sc);
                    break;
                case 2:
                    displayBillingRecord(sc);
                    break;
                case 3:
                    removeBillingRecord(sc);
                    break;
                case 4:
                    displayAllRecords();
                    break;
                case 5:
                    System.out.println("\nExiting... Thank you!");
                    break;
                default:
                    System.out.println("\nInvalid choice! Please enter 1-5.");
            }
        } while (choice != 5);
        
        sc.close();
    }
    
    
    private static void addBillingRecord(Scanner sc) {
        System.out.println("\n--- Add New Billing Record ---");
        
        System.out.print("Enter Patient ID: ");
        int patientId = sc.nextInt();
        sc.nextLine();
        
        System.out.print("Enter Patient Name: ");
        String patientName = sc.nextLine();
        
        System.out.print("Enter Visit Date (YYYY-MM-DD): ");
        String visitDate = sc.nextLine();
        
        System.out.print("Enter Bill Amount (OMR): ");
        double billAmount = sc.nextDouble();
        sc.nextLine();
        
        // Create new BillingRecord object
        BillingRecord record = new BillingRecord(patientId, patientName, 
                                                  visitDate, billAmount);
        
        // Add to HashMap
        billingRecords.put(patientId, record);
        
        System.out.println("\n✓ Billing record added successfully!");
        System.out.println("Patient: " + patientName);
        System.out.println("Bill Amount: " + billAmount + " OMR");
    }
    
    /**
     * Display a specific billing record based on patient ID
     */
    private static void displayBillingRecord(Scanner sc) {
        System.out.println("\n--- Display Billing Record ---");
        
        System.out.print("Enter Patient ID to display: ");
        int patientId = sc.nextInt();
        
        // Check if record exists
        if (billingRecords.containsKey(patientId)) {
            BillingRecord record = billingRecords.get(patientId);
            System.out.println("\n========== Billing Record ==========");
            System.out.println(record);
            System.out.println("====================================");
        } else {
            System.out.println("\n✗ No record found for Patient ID: " + patientId);
        }
    }
    
    
    private static void removeBillingRecord(Scanner sc) {
        System.out.println("\n--- Remove Billing Record ---");
        
        System.out.print("Enter Patient ID to remove: ");
        int patientId = sc.nextInt();
        
        // Remove from HashMap
        BillingRecord removed = billingRecords.remove(patientId);
        
        if (removed != null) {
            System.out.println("\n✓ Record removed successfully!");
            System.out.println("Removed: " + removed.getPatientName());
        } else {
            System.out.println("\n✗ No record found for Patient ID: " + patientId);
        }
    }
    
    
    
    private static void displayAllRecords() {
        System.out.println("\n--- All Billing Records ---");
        
        if (billingRecords.isEmpty()) {
            System.out.println("No records available.");
            return;
        }
        
        System.out.println("\nTotal Records: " + billingRecords.size());
        System.out.println("====================================");
        
        // Iterate through HashMap using entrySet()
        int count = 1;
        for (Map.Entry<Integer, BillingRecord> entry : billingRecords.entrySet()) {
            System.out.println("\nRecord #" + count);
            System.out.println(entry.getValue());
            System.out.println("------------------------------------");
            count++;
        }
    }
}

/**
 * BillingRecord class to store billing information
 */
class BillingRecord {
    private int patientId;
    private String patientName;
    private String visitDate;
    private double billAmount;
    
    // Constructor
    public BillingRecord(int patientId, String patientName, 
                         String visitDate, double billAmount) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.visitDate = visitDate;
        this.billAmount = billAmount;
    }
    
    // Getters
    public int getPatientId() {
        return patientId;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public String getVisitDate() {
        return visitDate;
    }
    
    public double getBillAmount() {
        return billAmount;
    }
    
    // toString method for display
    @Override
    public String toString() {
        return "Patient ID: " + patientId + "\n" +
               "Patient Name: " + patientName + "\n" +
               "Visit Date: " + visitDate + "\n" +
               "Bill Amount: " + billAmount + " OMR";
    }
}