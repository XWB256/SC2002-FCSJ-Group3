package Menus;

import Loaders.ApplicantLoader;
import Loaders.HDBManagerLoader;
import Loaders.HDBOfficerLoader;
import Users.Applicant;
import Users.HDBManager;
import Users.HDBOfficer;
import Users.User;
import Utilities.Utility;

import java.util.List;
import java.util.Scanner;

import static Utilities.Display.displayHeader;
import static Utilities.Display.displaySeperator;

public class StartMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {

            displaySeperator();
            displayHeader("Welcome to BTO System");


            System.out.println("Welcome, today is "+ Utility.CURRENT_DATE);
            displaySeperator();

            //System.out.println("Welcome to BTO System");
            System.out.println("1. Login as Applicant");
            System.out.println("2. Login as Officer");
            System.out.println("3. Login as Manager");
            System.out.println("0. Exit");
            System.out.print("Select option: ");
            String choice = scanner.nextLine().trim();
            displaySeperator();

            switch (choice) {
                case "1":
                    loginAsApplicant();
                    break;
                case "2":
                    loginAsOfficer();
                    break;
                case "3":
                    loginAsManager();
                    break;
                case "0":
                    System.out.println("System exiting...Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void loginAsApplicant() {
        List<Applicant> applicants = ApplicantLoader.loadApplicants();
        Applicant user = authenticate(applicants);
        if (user != null) {
            System.out.println("Login Successful!");
            Applicant applicant = new Applicant(user.getName(), user.getNRIC(), user.getAge(), user.getMaritalStatus(), user.getPassword());
            new ApplicantMenu(applicant).run();
        }
    }

    private void loginAsOfficer() {
        List<HDBOfficer> officers = HDBOfficerLoader.loadOfficers();
        HDBOfficer user = authenticate(officers);
        if (user != null) {
            System.out.println("Login Successful!");
            HDBOfficer officer = new HDBOfficer(user.getName(), user.getNRIC(), user.getAge(), user.getMaritalStatus(), user.getPassword());
            new HDBOfficerMenu(officer).run();
        }
    }

    private void loginAsManager() {
        List<HDBManager> managers = HDBManagerLoader.loadManagers();
        HDBManager user = authenticate(managers);
        if (user != null) {
            System.out.println("Login Successful!");
            HDBManager manager = new HDBManager(user.getName(), user.getNRIC(), user.getAge(), user.getMaritalStatus(), user.getPassword());
            new HDBManagerMenu(manager).run();
        }
    }

    // generic
    private <T extends User> T authenticate(List<T> users) {
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        for (T user : users) {
            if (user.getNRIC().equals(nric) && user.getPassword().equals(password)) {
                return user;
            }
        }

        System.out.println("Invalid NRIC or Password. Try again.");
        return null;
    }
}
