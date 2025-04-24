package Menus;


import Classes.Application;
import Classes.Enquiry;
import Classes.Project;

import Loaders.EnquiryLoader;
import Users.Applicant;
import Utilities.Filter;
import Utilities.Utility;


import java.util.List;

import static Utilities.Display.*;
import static Utilities.ReadInput.readIntInput;

public class ApplicantMenu{
    private Applicant applicant;
    private final boolean isOfficer;

    public ApplicantMenu(Applicant applicant, boolean isOfficer) {
        this.applicant = applicant;
        this.isOfficer = isOfficer;
    }

    public ApplicantMenu(Applicant applicant) {
        this(applicant, false);
    }

    public void run(){
        boolean loggedIn = true;
        List<Project> currentFilteredProjects = null;
        List<Enquiry> currentFilteredEnquiries = null;


        displaySeperator();
        System.out.println("Welcome, Applicant " + applicant.getName());

        if (applicant.getCurrentApplication() != null){
            System.out.println("Current Project : " + applicant.getCurrentApplication().getProjectName() + " ( " + applicant.getCurrentApplication().getApplicationStatus() + " )") ;
        } else {
            System.out.println("Current Project: You have not applied for a project");
        }
        displaySeperator();

        System.out.println("What would you like to do today?");

        do {
            displaySeperator();
            displayHeader("APPLICANT MENU");
            displaySubHeader("Applicant Functions");

            System.out.printf("%-40s %s%n", "01. View Available Projects", "06. Book a Flat");
            System.out.printf("%-40s %s%n", "02. Apply for a Project", "07. Create Enquiry");
            System.out.printf("%-40s %s%n", "03. View Applied Project", "08. Edit Enquiry");
            System.out.printf("%-40s %s%n", "04. View Application Details", "09. Delete Enquiry");
            System.out.printf("%-40s %s%n", "05. Withdraw Application", "10. View Enquiries");
            System.out.println();
            displaySubHeader("User Functions");
            System.out.println("11. Change Password");
            System.out.println("0. Log Out");
            displaySeperator();
            int choice = readIntInput("Enter Your Choice: ");
            displaySeperator();

            List<Project> visibleList = applicant.viewAvailableProjects(applicant);
            List<Enquiry> myEnquires = applicant.getMyEnquiry();

            switch (choice) {
                case 1:
                    displaySelection("View Projects Available to You");
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
                    break;

                case 2:
                    displaySelection("Apply for a Project");
                    displaySeperator();
                    displayHeader("Projects List");
                    if(currentFilteredProjects!=null){
                        Filter.printCurrentList(currentFilteredProjects);
                    }
                    else {
                        currentFilteredProjects = Filter.filterRun(visibleList);
                    }
                    applicant.applyForProject();
                    break;

                case 3:
                    displaySelection("View Applied Project Details");
                    displaySeperator();
                    applicant.viewAppliedProject();
                    break;
                case 4:
                    displaySelection("View Application Details");
                    displaySeperator();
                    applicant.viewApplicationDetails();
                    break;
                case 5:
                    displaySelection("Request a Withdrawal");
                    displaySeperator();
                    applicant.withdrawApplication();
//                    applicant.requestWithdrawal(applicant,sc);
                    break;

                case 6:
                    displaySelection("Book a Flat");
                    displaySeperator();
                    applicant.bookFlat();
                    break;

                case 7:
                    displaySelection("Create Enquiry");
                    displaySeperator();

                    displayHeader("Projects List");
                    if(currentFilteredProjects!=null){
                        Filter.printCurrentList(currentFilteredProjects);
                    }
                    else currentFilteredProjects = Filter.filterRun(visibleList);

                    applicant.createEnquiry();

                    break;

                case 8:
                    displaySelection("Edit Enquiry");
                    displaySeperator();

                    displayHeader("Your Enquires");
                    if(currentFilteredEnquiries!=null){
                        Filter.printCurrentList(currentFilteredEnquiries);
                    }
                    else currentFilteredEnquiries=Filter.filterRun(myEnquires);

                    applicant.editEnquiry();
                    currentFilteredEnquiries = Filter.updateCurrentFilter(currentFilteredEnquiries, EnquiryLoader.loadEnquires());
                    break;

                case 9:
                    displaySelection("Delete Enquiry");
                    displaySeperator();

                    displayHeader("Your Enquires");
                    if(currentFilteredEnquiries!=null){
                        Filter.printCurrentList(currentFilteredEnquiries);
                    }
                    else currentFilteredEnquiries=Filter.filterRun(myEnquires);

                    applicant.deleteEnquiry();
                    currentFilteredEnquiries = Filter.updateCurrentFilter(currentFilteredEnquiries, EnquiryLoader.loadEnquires());
                    break;

                case 10:
                    displaySelection("View Enquiries");
                    displaySeperator();

                    Filter.printCurrentList(currentFilteredEnquiries);
                    if(currentFilteredEnquiries!=null){
                        int alt = readIntInput("Enter 1 to filter on current list, enter 0 to reset: ");
                        if(alt==1) {
                            displayHeader("Your Enquires");
                            currentFilteredEnquiries = Filter.filterRun(currentFilteredEnquiries);
                        }
                        else if (alt==0) {
                            displayHeader("Your Enquires");
                            currentFilteredEnquiries=Filter.filterRun(myEnquires);
                        }
                        else {
                            System.out.println("Invalid choice!");
                        }
                        break;
                    }

                    displayHeader("Your Enquires");
                    currentFilteredEnquiries=Filter.filterRun(myEnquires);
                    break;

                case 11:
                    displaySelection("Change Password");
                    displaySeperator();
                    if (isOfficer) {
                        displaySeperator();
                        System.out.println("You are an officer and cannot change password here!");
                    } else {
                        applicant.changePassword(applicant);
                    }
                    break;

                case 0:
                    displaySelection("Log Out");
                    displaySeperator();
                    System.out.println("Thank you for using our Application System!");
                    loggedIn = false;
                    break;

                default:
                    System.out.println("Invalid Input! Please enter a number from 1 to 10.");
            }

        } while (loggedIn);
        System.out.println();

    }
}
