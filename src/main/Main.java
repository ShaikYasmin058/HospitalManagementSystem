package main;

import java.sql.SQLException;
import java.util.Scanner;
import database.DBConnection;
import service.HospitalService;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HospitalService service = new HospitalService();

        System.out.println("====================================");
        System.out.println("Hospital Management System");
        System.out.println("====================================");

        System.out.print("Enter MySQL username (default root): ");
        String dbUsername = scanner.nextLine().trim();
        if (dbUsername.isEmpty()) {
            dbUsername = "root";
        }

        System.out.print("Enter MySQL password (leave blank if none): ");
        String dbPassword = scanner.nextLine();

        System.setProperty("DB_USERNAME", dbUsername);
        System.setProperty("DB_PASSWORD", dbPassword);

        boolean dbReady = false;
        try {
            DBConnection.initializeDatabase();
            dbReady = true;
            System.out.println("Database initialized successfully.");
        } catch (SQLException ex) {
            System.out.println("MySQL connection unavailable. Running in demo mode with limited functionality.");
            System.out.println(ex.getMessage());
        }

        if (!dbReady && !service.login()) {
            System.out.println("Exiting application.");
            return;
        }

        if (dbReady && !service.login()) {
            System.out.println("Exiting application.");
            return;
        }

        while (true) {
            System.out.println("\n1. Add Patient");
            System.out.println("2. View All Patients");
            System.out.println("3. Search Patient by ID");
            System.out.println("4. Update Patient");
            System.out.println("5. Delete Patient");
            System.out.println("6. Add Doctor");
            System.out.println("7. View All Doctors");
            System.out.println("8. Search Doctor by ID");
            System.out.println("9. Update Doctor");
            System.out.println("10. Delete Doctor");
            System.out.println("11. Book Appointment");
            System.out.println("12. View Appointments");
            System.out.println("13. Cancel Appointment");
            System.out.println("14. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        service.addPatient();
                        break;
                    case "2":
                        service.viewAllPatients();
                        break;
                    case "3":
                        service.searchPatient();
                        break;
                    case "4":
                        service.updatePatient();
                        break;
                    case "5":
                        service.deletePatient();
                        break;
                    case "6":
                        service.addDoctor();
                        break;
                    case "7":
                        service.viewAllDoctors();
                        break;
                    case "8":
                        service.searchDoctor();
                        break;
                    case "9":
                        service.updateDoctor();
                        break;
                    case "10":
                        service.deleteDoctor();
                        break;
                    case "11":
                        service.bookAppointment();
                        break;
                    case "12":
                        service.viewAppointments();
                        break;
                    case "13":
                        service.cancelAppointment();
                        break;
                    case "14":
                        System.out.println("Thank you for using Hospital Management System.");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception ex) {
                System.out.println("An unexpected error occurred: " + ex.getMessage());
            }
        }
    }
}
