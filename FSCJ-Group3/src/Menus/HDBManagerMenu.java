package Menus;

import Classes.Application;
import Classes.Enquiry;
import Classes.Project;
import Loaders.EnquiryLoader;
import Loaders.ProjectLoader;
import Users.HDBManager;
import Utilities.Filter;
import Utilities.Utility;

import java.util.List;
import java.util.Scanner;

import static Utilities.Display.*;
import static Utilities.ReadInput.readIntInput;

public class HDBManagerMenu {
    private final HDBManager manager;

    public HDBManagerMenu(HDBManager manager) {
        this.manager = manager;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        int choice;
        boolean loggedIn = true;
        List<Project> currentFilteredProjects = null;
        List<Application> currentFilteredApplications = null;
        List<Enquiry> currentFilteredEnquires = null;

        displaySeperator();
        System.out.println("Welcome, Manager " + manager.getName());
        displaySeperator();
        if (manager.getActiveProject() == null) {
            System.out.println("Current Active Project: No Active Project");
        } else {
            System.out.println("Current Active Project: " + manager.getActiveProject().getProjectName());
        }
        displaySeperator();

        do {
            displayHeader("MANAGER MENU");
            System.out.println(BOLD + UNDERLINE + "Manager Functions" + RESET);
            System.out.printf("%-40s %s%n", "01. View Your Projects", "07. View Officer Registrations");
            System.out.printf("%-40s %s%n", "02. Toggle A Project's Visibility", "08. View Applicant Applications");
            System.out.printf("%-40s %s%n", "03. Create A New Project", "09. View Applicant Withdrawal Applications");
            System.out.printf("%-40s %s%n", "04. Edit A Project", "10. Generate Project Report");
            System.out.printf("%-40s %s%n", "05. Delete A Project", "11. View All Enquiries");
            System.out.printf("%-40s %s%n", "06. View All Projects", "12. View / Reply Current Project Enquiry");
            System.out.println();
            System.out.println(BOLD + UNDERLINE + "User Functions" + RESET);

            System.out.println("13. Change Password");
            System.out.println("0. Log Out");
            displaySeperator();
            choice = readIntInput("Enter Your Choice: ");
            displaySeperator();

            switch (choice) {
                case 1 -> {
                    displaySelection("View Your Projects");
                    displaySeperator();
                    manager.viewOwnProject();
                }
                case 2 -> {
                    displaySelection("Toggle A Project's Visibility");
                    displaySeperator();
                    manager.toggleVisibility();
                }
                case 3 -> {
                    displaySelection("Create A New Project");
                    displaySeperator();
                    manager.createProject();
                }
                case 4 -> {
                    displaySelection("Edit A Project");
                    displaySeperator();
                    manager.editProject();
                }
                case 5 ->{
                    displaySelection("Delete A Project");
                    displaySeperator();
                    manager.deleteProject();
                }
                case 6 -> {
                    displaySelection("View All Projects");
                    displaySeperator();
                    manager.viewAllProjects();
                    Filter.printCurrentList(currentFilteredProjects);
                    currentFilteredProjects = Filter.filterRun(ProjectLoader.loadProjects());
                }
                case 7 ->{
                    displaySelection("View Officer Registrations");
                    displaySeperator();
                    manager.viewOfficerRegistrations();
                }
                case 8 -> {
                    displaySelection("View Applicant Applications");
                    displaySeperator();
                    manager.viewApplicantApplications();
                }
                case 9 -> {
                    displaySelection("View Applicant Withdrawal Applications");
                    displaySeperator();
                    manager.viewApplicantWithdrawalApplications();
                }
                case 10 -> {
                    displaySelection("Generate Project Report");
                    displaySeperator();
                    manager.generateReport();
                }
                case 11 -> {
                    displaySelection("View All Enquiries");
                    displaySeperator();
                    manager.viewAllEnquiries();
//                    Filter.printCurrentList(currentFilteredEnquires);
//                    currentFilteredEnquires = Filter.filterRun(EnquiryLoader.loadEnquires());
                }
                case 12 -> {
                    displaySelection("View / Reply Current Project Enquiry");
                    displaySeperator();
                    manager.viewCurrentProjectEnquiries();
                }
                case 13 -> {
                    displaySelection("Change Password");
                    displaySeperator();
                    manager.changePassword(manager);
                }
                case 0 -> {
                    displaySelection("Log Out");
                    System.out.println("Logging out... Goodbye " + manager.getName() + "! Have a nice day!");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (loggedIn);
        System.out.println();
    }
}
