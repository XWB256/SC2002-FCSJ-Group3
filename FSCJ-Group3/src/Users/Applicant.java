package Users;

import Classes.Application;
import Classes.Application.ApplicationStatus;
import Classes.Enquiry;
import Classes.Project;
import Classes.Project.Visibility;
import Loaders.*;

import java.time.LocalDate;
import java.util.*;

import static Classes.Enquiry.deleteEnquiryCSV;
import static Loaders.ApplicationLoader.saveApplicationToCSV;
import static Loaders.EnquiryLoader.addEnquiryToCSV;
import static Loaders.EnquiryLoader.updateEnquiriesToCSV;
import static Utilities.Display.displaySeperator;
import static Utilities.ReadInput.readStringInput;
import static Utilities.Utility.selectProject;

public class Applicant extends User{

    private UserGroup userGroup;
    public enum UserGroup { CANNOT_APPLY, SINGLE_ABOVE35, MARRIED }
    private boolean isOfficer;
//    private Application currentApplication;

    public Applicant(String name, String NRIC, int age, MaritalStatus maritalStatus, String password){
        super(name, NRIC, age, password, maritalStatus);
        setUserGroup();
        setIsOfficer();
//        setApplication();
    }


    public Application getCurrentApplication() {
        return ApplicationLoader.loadApplications().stream()
                .filter(application -> application.getApplicantNRIC().equals(getNRIC())
                        )
                .findFirst()
                .orElse(null);
    }

    public List<Enquiry> getMyEnquiry() {
        return EnquiryLoader.loadEnquires().stream().filter(enquiry -> enquiry.getNRIC().equals(getNRIC())).toList();
    }

    private void setIsOfficer(){
        this.isOfficer = HDBOfficerLoader.loadOfficers().stream().anyMatch(officer -> officer.getNRIC().equals(getNRIC()));
    }

    private void setUserGroup(){
        if ((getAge() < 21 || getAge() < 35) && getMaritalStatus().equals(MaritalStatus.SINGLE)){
            this.userGroup = UserGroup.CANNOT_APPLY;
        } else if ((getAge() >= 35) && getMaritalStatus().equals(MaritalStatus.SINGLE)){
            this.userGroup = UserGroup.SINGLE_ABOVE35;
        } else {
            this.userGroup = UserGroup.MARRIED;
        }
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public List<Project> viewAvailableProjects(Applicant a) {
        List<Project> availableProjects = new ArrayList<>();
        Set<String> typesForMARRIED = Set.of("2-ROOM", "3-ROOM", "4-ROOM", "5-ROOM", "Executive");

        for (Project project : ProjectLoader.loadProjects()) {
            if (a.getUserGroup() == UserGroup.SINGLE_ABOVE35 && (project.getType1().equals("2-ROOM") || project.getType2().equals("2-ROOM")) && project.getVisibility() ==Visibility.ON) {
                availableProjects.add(project);
                //System.out.println(project);
            }
            else if (a.getAge() > 21 && a.getUserGroup() == UserGroup.MARRIED && (typesForMARRIED.contains(project.getType1()) || typesForMARRIED.contains(project.getType2()))&& project.getVisibility() ==Visibility.ON){
                availableProjects.add(project);
                //System.out.println(project);
            }
            else{
                //System.out.println("You are unable to apply for flats!");
            }
        }
        return availableProjects;
    }

    public void applyForProject() {
        boolean hasApplied = ApplicationLoader.loadApplications().stream()
                .anyMatch(application ->
                        application.getApplicantNRIC().equals(getNRIC())
                                && application.getApplicationStatus() != ApplicationStatus.UNSUCCESSFUL
                                && application.getApplicationStatus() != ApplicationStatus.PROJECT_DELETED
                );

        if (hasApplied) {
            System.out.println("You already have a application in process. You cannot apply to another application.");
            return;
        }

        Project selectedProject = selectProject(ProjectLoader.loadProjects());

        if (selectedProject == null) {
            return;
        }

        if (!(selectedProject.getApplicationOpenDate().isEqual(LocalDate.now()) || selectedProject.getApplicationOpenDate().isBefore(LocalDate.now()) && selectedProject.getApplicationCloseDate().isAfter(LocalDate.now()))) {
            System.out.println("Project is not in application period");
            return;
        }

        if (isOfficer) {
            boolean sameProject = ProjectLoader.loadProjects().stream()
                    .anyMatch(project -> Arrays.asList(project.getOfficerNRIC().split(";"))
                            .contains(getNRIC()));
            if (sameProject) {
                System.out.println("You are already an officer for this project. You cannot apply as an applicant.");
                return;
            }
        }

        String FlatType = "2-ROOM";
        if (getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            FlatType = readStringInput("What Type of Flat Do you want? (2-ROOM or 3-ROOM) : ").toUpperCase();
        }

        Application newApplication = new Application(selectedProject.getProjectName(),getName(), getNRIC(), getAge(), getMaritalStatus(), FlatType,ApplicationStatus.PENDING);
        saveApplicationToCSV(newApplication);
        System.out.println("\nYou have successfully made an application!");
    }

    public void viewAppliedProject(){
        Application app = getCurrentApplication();
        if (app == null){
            System.out.println("No application for any project!");
        } else {
            for (Project project : ProjectLoader.loadProjects()) {
                if(project.getProjectName().equals(app.getProjectName())){
                    System.out.println(project);
                    break;
                }
            }
        }
    }

    public void viewApplicationDetails(){
        Application app = getCurrentApplication();
        if (app == null){
            System.out.println("No applications have been made!");
        } else {
            System.out.println(app);
        }
    }

    public void bookFlat(){
        if (getCurrentApplication() == null){
            System.out.println("You haven't applied yet.");
            return;
        }

        Application currentApp = getCurrentApplication();
        ApplicationStatus status = currentApp.getApplicationStatus();

        switch (status) {
            case SUCCESSFUL -> {
                currentApp.setApplicationStatus(ApplicationStatus.APPLY_FOR_BOOKING);
                saveApplicationToCSV(currentApp);
                System.out.println("Successfully applied for booking!");
            }

            case BOOKED -> System.out.println("You have already booked.");

            default ->
                    System.out.println("Booking Failed. Your application must be successful before you can book.");
        }

    }

    public void withdrawApplication(){
        Application app = getCurrentApplication();
        if (app == null){
            System.out.println("You haven't applied yet.");
            return;
        }

        if (app.getApplicationStatus().name().contains(("IN_WITHDRAWAL"))){
            System.out.println("You have already had one pending application.");
            return;
        }

        String choice = readStringInput("Confirm to Withdraw? (Yes/No): ").toLowerCase();

        switch (choice){
            case "y","yes" -> {
                app.setApplicationStatus(
                        ApplicationStatus.valueOf(app.getApplicationStatus().name()+"_IN_WITHDRAWAL"));
                saveApplicationToCSV(app);
                System.out.println("Application Withdrawal Request Sent");
            }
            case "n", "no" -> {
                System.out.println("Discarded Withdrawal.");
            }
            default -> System.out.println("Please select Yes or No");
        }

    }

    public void createEnquiry(){
        /*
        displayHeader("Projects List");
        ProjectLoader.loadProjects().forEach(project -> {
            displayFullProjectDetails(project);
            displaySeperator();
        });

         */

        Project selectedProject = selectProject(ProjectLoader.loadProjects());

        if (selectedProject == null) {
            return;
        }

        String message = readStringInput("Enter your Message/Question: ").replace(",", ";");
        Enquiry newEnquiry = new Enquiry(null, selectedProject.getProjectName(), getName(), getNRIC(), message, null);
        addEnquiryToCSV(newEnquiry);
        System.out.println("Enquiry has been created!");

    }

    public void editEnquiry(){
        /*
        displayHeader("Your Enquires");
        EnquiryLoader.loadEnquires().stream().filter(enquiry -> enquiry.getNRIC().equals(getNRIC())).forEach(enquiry -> {
            System.out.println(enquiry);
            displaySeperator();
        });
         */

        Enquiry selectedEnquiry;
        do{
            String EnqID = readStringInput("Enter Enquiry ID (or 0 to return): ");

            if(EnqID.equals("0")) {
                displaySeperator();
                System.out.println("Returned. ");
                return;
            }

            selectedEnquiry = EnquiryLoader.loadEnquires().stream().filter(enquiry -> enquiry.getEnquiryID().equals(EnqID)).findFirst().orElse(null);

            if (selectedEnquiry == null){
                System.out.println("Enquiry Not Found. Make sure the ID is exact");
                displaySeperator();
            }
        } while(selectedEnquiry == null);

        String message = readStringInput("Enter New Message (or 0 to return): ").replace(",", ";");
        if(message.equals("0")) {
            displaySeperator();
            System.out.println("Returned. ");
            return;
        }

        selectedEnquiry.setContent(message);
        updateEnquiriesToCSV(selectedEnquiry);
        System.out.println("Enquiry Updated");

    }

    public void deleteEnquiry(){
        /*
        displayHeader("Your Enquires");
        EnquiryLoader.loadEnquires().stream().filter(enquiry -> enquiry.getNRIC().equals(getNRIC())).forEach(enquiry -> {
            System.out.println(enquiry);
            displaySeperator();
        });
         */

        Enquiry selectedEnquiry;
        do{
            String EnqID = readStringInput("Enter Enquiry ID (or 0 to return): ");

            if(EnqID.equals("0")) {
                displaySeperator();
                System.out.println("Returned. ");
                return;
            }

            selectedEnquiry = EnquiryLoader.loadEnquires().stream().filter(enquiry -> enquiry.getEnquiryID().equals(EnqID)).findFirst().orElse(null);

            if (selectedEnquiry == null){
                System.out.println("Enquiry Not Found. Make sure the ID is exact");
                displaySeperator();
            }
        } while(selectedEnquiry == null);

        deleteEnquiryCSV(selectedEnquiry);
        System.out.println("Enquiry deleted!");

    }


}
