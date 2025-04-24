package Menus;

import Classes.Project;
import Users.Applicant;
import Users.HDBOfficer;
import Utilities.Filter;
import Utilities.Utility;

import java.util.List;

import static Utilities.Display.*;
import static Utilities.ReadInput.readIntInput;


public class HDBOfficerMenu {
    private final HDBOfficer officer;

    public HDBOfficerMenu(HDBOfficer officer){
        this.officer = officer;
    }

    public void run(){
        int choice;
        boolean loggedIn = true;
        List<Project> currentFilteredProjects = null;

        displaySeperator();
        System.out.println("Welcome Officer " + officer.getName());
        //displaySeperator();

        do{
            officer.refreshAllOfficerData();
            displaySeperator();
            displayHeader("OFFICER MENU");
            System.out.println(BOLD + UNDERLINE + "Officer Functions" + RESET);
            System.out.printf("%-40s %s%n", "01. View All Accessible Projects", "05. Reply to Project Enquiries");
            System.out.printf("%-40s %s%n", "02. Apply to Join Project as Officer", "06. Accept Booking Application");
            System.out.printf("%-40s %s%n", "03. View Officer Application Status", "07. Generate Receipt");
            System.out.println("04. View Assigned Project Details");
            System.out.println();
            System.out.println(BOLD + UNDERLINE + "User Functions" + RESET);
            System.out.println("08. Change Password");
            System.out.println("09. Enter Applicant Menu");
            System.out.println("0. Log Out");
            displaySeperator();
            choice = readIntInput("Enter Your Choice: ");
            displaySeperator();

            List<Project> visibleList = officer.viewAllProjects();

            switch (choice) {
                case 1 -> {
                    displaySelection("View All Accessible Projects");
                    displaySeperator();
                    Filter.printCurrentList(currentFilteredProjects);
                    if(currentFilteredProjects!=null){
                        int alt = readIntInput("Enter 1 to filter on current list, enter 0 to reset: ");
                        displaySeperator();
                        if(alt==1) {
                            displayHeader("Projects List");
                            currentFilteredProjects = Filter.filterRun(currentFilteredProjects);
                        }
                        else if (alt==0) {
                            displayHeader("Projects List");
                            currentFilteredProjects = Filter.filterRun(visibleList);
                        }
                        else {
                            System.out.println("Invalid choice!");
                        }
                        break;
                    }

                    displayHeader("Projects List");
                    currentFilteredProjects = Filter.filterRun(visibleList);
                }
                case 2 -> {
                    displaySelection("Apply to Join Project as Officer");
                    displaySeperator();

                    /*
                    displayHeader("Projects List");
                    if(currentFilteredProjects!=null){
                        Filter.printCurrentList(currentFilteredProjects);
                    }
                    else {
                        currentFilteredProjects = Filter.filterRun(visibleList);
                    }
                     */

                    officer.registerForProject();
                }
                case 3 -> {
                    displaySelection("View Officer Application Status");
                    displaySeperator();
                    officer.viewOfficerApplicationStatus();
                }
                case 4 -> {
                    displaySelection("View Assigned Project Details");
                    displaySeperator();
                    officer.viewAssignedProjectDetails();
                }
                case 5 -> {
                    displaySelection("Reply to Project Enquiries");
                    displaySeperator();
                    officer.replyToEnquires();
                }
                case 6 -> {
                    displaySelection("Accept Booking Application");
                    displaySeperator();
                    officer.acceptBookingApplication();
                }
                case 7 -> {
                    displaySelection("Generate Receipt");
                    displaySeperator();
                    officer.generateReceiptFromNric();
                }
                case 8 -> {
                    displaySelection("Change Password");
                    displaySeperator();
                    officer.changePassword(officer);
                }
                case 9 -> {
                    displaySelection("Enter Applicant Menu");
                    displaySeperator();
                    Applicant selfApplicant = officer.CreateApplicant();
                    System.out.println("Switching to Applicant Menu...");
                    new ApplicantMenu(selfApplicant,true).run();
                }
                case 0 -> {
                    displaySelection("Log Out");
                    displaySeperator();
                    System.out.println("logging out... Goodbye " + officer.getName() + "! Have a nice day!");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }while(loggedIn);
        System.out.println();
    }
}